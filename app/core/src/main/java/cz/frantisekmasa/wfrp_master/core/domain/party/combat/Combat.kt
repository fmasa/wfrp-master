package cz.frantisekmasa.wfrp_master.core.domain.party.combat

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Combat(
    val encounterId: UUID,
    private var combatants: List<Combatant>,
    private val turn: Int = 1,
    private val round: Int = 1
): Parcelable {

    init {
        require(turn >= 1 && turn <= combatants.size)
        require(round >= 1)

        combatants = combatants.toList()
    }

    val npcIds: List<NpcId>
        get() = combatants.mapNotNull { if (it is Combatant.Npc) it.npcId else null  }

    fun getCombatants() = combatants
    fun getTurn() = turn
    fun getRound() = round

    fun previousTurn(): Combat {
        if (turn == 1 && round == 1) {
            return this
        }

        return if (turn == 1)
            copy (turn = combatants.size, round = round - 1)
        else copy (turn = turn -1)
    }

    fun nextTurn(): Combat {
        return if (turn == combatants.size)
            copy (turn = 1, round = round + 1)
        else copy (turn = turn + 1)
    }

    fun updateCombatant(combatant: Combatant): Combat {
        val index = combatants.indexOfFirst { it.areSameEntity(combatant) }

        require(index != -1) { "Combatant of same entity as $combatant was not found"}

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
        other.size != size || other.containsAll(this)
}
