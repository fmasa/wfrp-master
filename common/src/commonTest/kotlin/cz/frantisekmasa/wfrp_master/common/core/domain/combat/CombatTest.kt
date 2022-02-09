package cz.frantisekmasa.wfrp_master.common.core.domain.combat

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combat
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
    fun `removeNpc() does nothing if npc is not in combat`() {
        assertEquals(combat, combat.removeNpc(NpcId(encounterId, UUID.randomUUID())))
    }

    @Test
    fun `removeNpc() removes NPC from combat`() {
        assertEquals(
            combat.copy(combatants = listOf(combat.getCombatants()[1], combat.getCombatants()[2])),
            combat.removeNpc(npcId1),
        )
    }

    @Test
    fun `turns and rounds are same when removed NPC was not on turn yet`() {
        assertEquals(
            combat.copy(
                combatants = listOf(combat.getCombatants()[1], combat.getCombatants()[2]),
            ),
            combat.copy(turn = 2)
                .removeNpc(npcId1)
        )
    }

    @Test
    fun `turns and rounds are same when removed NPC is on turn and is not last`() {
        assertEquals(
            1000,
            combat.copy(round = 1000)
                .removeNpc(npcId1)
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
                .removeNpc(npcId1)
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
                .removeNpc(npcId2)
        )
    }

    @Test
    fun `combat is ended when last combatant is removed`() {
        assertNull(
            combat.copy(combatants = listOf(combat.getCombatants()[0]))
                .removeNpc(npcId1)
        )
    }
}
