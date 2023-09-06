package cz.frantisekmasa.wfrp_master.common.core.domain.combat

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Advantage
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CombatTest {
    private val combat = Combat(
        encounterId = uuid4(),
        combatants = listOf(
            Combatant(characterId = uuid4().toString(), initiative = 10, advantage = Advantage.ZERO, id = uuid4()),
            Combatant(characterId = uuid4().toString(), initiative = 10, advantage = Advantage.ZERO, id = uuid4()),
            Combatant(characterId = uuid4().toString(), initiative = 10, advantage = Advantage.ZERO, id = uuid4()),
        )
    )

    @Test
    fun `removeCombatant() does nothing if combatant is not in combat`() {
        assertEquals(
            combat,
            combat.removeCombatant(uuid4()),
        )
    }

    @Test
    fun `removeCombatant() removes combatant from combat`() {
        assertEquals(
            combat.copy(combatants = listOf(combat.getCombatants()[1], combat.getCombatants()[2])),
            combat.removeCombatant(combat.getCombatants()[0].id!!),
        )
    }

    @Test
    fun `turns and rounds are same when removed NPC was not on turn yet`() {
        assertEquals(
            combat.copy(
                combatants = listOf(combat.getCombatants()[1], combat.getCombatants()[2]),
            ),
            combat.copy(turn = 2)
                .removeCombatant(combat.getCombatants()[0].id!!)
        )
    }

    @Test
    fun `turns and rounds are same when removed NPC is on turn and is not last`() {
        assertEquals(
            1000,
            combat.copy(round = 1000)
                .removeCombatant(combat.getCombatants()[0].id!!)
                ?.getRound()
        )
    }

    @Test
    fun `turn is reduced by one when removed NPC was on turn before`() {
        assertEquals(
            combat.copy(
                turn = 1,
                combatants = listOf(combat.getCombatants()[1], combat.getCombatants()[2]),
            ),
            combat.copy(turn = 2)
                .removeCombatant(combat.getCombatants()[0].id!!)
        )
    }

    @Test
    fun `combat is moved to start of next turn when removed NPC is on turn and is last combatant`() {
        assertEquals(
            combat.copy(
                round = 2,
                turn = 1,
                combatants = listOf(combat.getCombatants()[0], combat.getCombatants()[1]),
            ),
            combat.copy(turn = 3)
                .removeCombatant(combat.getCombatants()[2].id!!)
        )
    }

    @Test
    fun `combat is ended when last combatant is removed`() {
        assertNull(
            combat.copy(combatants = listOf(combat.getCombatants()[0]))
                .removeCombatant(combat.getCombatants()[0].id!!)
        )
    }
}
