package cz.frantisekmasa.wfrp_master.combat.ui

import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.NpcRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combatant
import kotlinx.coroutines.flow.first
import java.util.*

class CombatViewModel(
    private val partyId: UUID,
    private val parties: PartyRepository,
    private val npcs: NpcRepository,
    private val characters: CharacterRepository,
) {

    suspend fun loadNpcsFromEncounter(encounterId: EncounterId): List<Npc> =
        npcs.findByEncounter(encounterId).first()

    suspend fun loadCharacters(): List<Character> = characters.inParty(partyId).first()

    suspend fun startCombat(
        encounterId: EncounterId,
        characters: List<Character>,
        npcs: List<Npc>
    ) {
        val party = parties.get(partyId)
        val combatants =
            characters.map { Combatant.Character(it.id, 1) } +
                npcs.map {
                    Combatant.Npc(NpcId(encounterId, it.id), 1)
            }

        party.startCombat(encounterId, rollInitiativeForCombatants(combatants))

        parties.save(party)
    }

    private fun rollInitiativeForCombatants(combatants: List<Combatant>): List<Combatant> {
        return combatants.map { it.withInitiative(1) }
            .sortedBy { it.initiative } // This is very naive approach, but for PoC it's enough
    }
}