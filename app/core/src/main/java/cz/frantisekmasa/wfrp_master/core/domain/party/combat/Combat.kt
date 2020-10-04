package cz.frantisekmasa.wfrp_master.core.domain.party.combat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Combat(
    private val encounterId: UUID,
    private var combatants: List<Combatant>,
    private val turn: Int = 1,
    private val round: Int = 1
): Parcelable {

    init {
        require(turn >= 1 && turn <= combatants.size)
        require(round >= 1)

        combatants = combatants.toList()
    }

    fun getEncounterId() = encounterId
    fun getCombatants() = combatants
    fun getTurn() = turn
    fun getRound() = round

    fun nextTurn(): Combat {
        return if (turn == combatants.size)
            copy (turn = 1, round = round + 1)
        else copy (turn = turn + 1)
    }
}
