package cz.frantisekmasa.wfrp_master.common.core.domain.party.combat

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
@Immutable
data class Combat(
    @Contextual
    val encounterId: UUID,
    private var combatants: List<Combatant>,
    private val turn: Int = 1,
    private val round: Int = 1,
    private val groupAdvantage: GroupAdvantage = GroupAdvantage(Advantage.ZERO, Advantage.ZERO),
) : Parcelable {

    init {
        require(turn >= 1 && turn <= combatants.size)
        require(round >= 1)

        combatants = combatants.toList()
    }

    fun getCombatants() = combatants
    fun getTurn() = turn
    fun getRound() = round

    fun previousTurn(): Combat {
        if (turn == 1 && round == 1) {
            return this
        }

        return if (turn == 1)
            copy(turn = combatants.size, round = round - 1)
        else copy(turn = turn - 1)
    }

    fun nextTurn(): Combat {
        return if (turn == combatants.size)
            copy(turn = 1, round = round + 1)
        else copy(turn = turn + 1)
    }

    fun updateCombatant(combatant: Combatant): Combat {
        val index = combatants.indexOfFirst { it.areSameEntity(combatant) }

        require(index != -1) { "Combatant of same entity as $combatant was not found" }

        return copy(
            combatants = combatants.toMutableList()
                .also { it[index] = combatant }
                .toList()
        )
    }

    fun reorderCombatants(reorderedCombatants: List<Combatant>): Combat {
        require(reorderedCombatants.containsSameItems(combatants)) { "Combatants must be same" }

        return copy(combatants = reorderedCombatants)
    }

    private fun <T> List<T>.containsSameItems(other: List<T>) =
        other.size == size || other.containsAll(this)

    fun removeCombatant(id: Uuid): Combat? {
        return removeFirstCombatant { it.id == id }
    }

    fun removeNpc(npcId: NpcId): Combat? {
        return removeFirstCombatant { it is Combatant.Npc && it.npcId == npcId }
    }

    private fun removeFirstCombatant(predicate: (Combatant) -> Boolean): Combat? {
        val removedIndex = combatants.indexOfFirst(predicate)

        if (removedIndex == -1) {
            return this
        }

        if (combatants.size == 1) {
            return null
        }

        val isOnTurn = turn == removedIndex + 1

        return copy(
            combatants = combatants.filterIndexed { index, _ -> index != removedIndex },
            round = if (isOnTurn && turn == combatants.size) round + 1 else round,
            turn = when {
                isOnTurn && turn == combatants.size -> 1 // Going to next combatant, effectively ending round
                turn > removedIndex + 1 -> turn - 1 // Reducing number of turns
                else -> turn // Active combatant is before NPC
            }
        )
    }
}
