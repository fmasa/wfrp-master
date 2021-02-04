package cz.frantisekmasa.wfrp_master.core.domain.party.combat

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class CombatTest {
    private val encounterId = EncounterId(PartyId.generate(), UUID.randomUUID())
    private val npcId1 = NpcId(encounterId, UUID.randomUUID())
    private val npcId2 = NpcId(encounterId, UUID.randomUUID())

    private val combat = Combat(
        encounterId = encounterId.encounterId,
        combatants = listOf(
            Combatant.Npc(npcId1, initiative = 10, advantage = 0),
            Combatant.Character(characterId = "foo", initiative = 10, advantage = 0),
            Combatant.Npc(npcId2, initiative = 10, advantage = 0),
        )
    )

    @Test
    fun removeNpcDoesNothingIfNpcIsNotInCombat() {
        assertEquals(combat, combat.removeNpc(NpcId(encounterId, UUID.randomUUID())))
    }

    @Test
    fun removeNpcRemovesNpcFromCombat() {
        assertEquals(
            combat.copy(combatants = listOf(combat.getCombatants()[1], combat.getCombatants()[2])),
            combat.removeNpc(npcId1),
        )
    }

    @Test
    fun testTurnsAndRoundsAreCalculatedCorrectlyRemovedNpcWasOnTurn() {
        // NPC is on turn
        assertEquals(
            1000,
            combat.copy(round = 1000)
                .removeNpc(npcId1)
                ?.getRound()
        )

        // NPC was on turn this round
        assertEquals(
            combat.copy(
                turn = 1,
                combatants = listOf(combat.getCombatants()[1], combat.getCombatants()[2]),
            ),
            combat.copy(turn = 2)
                .removeNpc(npcId1)
        )

        // NPC is on turn and is last combatant
        assertEquals(
            combat.copy(
                round = 2,
                turn = 1,
                combatants = listOf(combat.getCombatants()[0], combat.getCombatants()[1]),
            ),
            combat.copy(turn = 3)
                .removeNpc(npcId2)
        )

        // NPC is on turn and is last combatant
        assertNull(
            combat.copy(combatants = listOf(combat.getCombatants()[0]))
                .removeNpc(npcId1)
        )
    }
}