package cz.frantisekmasa.wfrp_master.common.encounters

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import kotlinx.coroutines.flow.Flow

class EncounterDetailScreenModel(
    private val encounterId: EncounterId,
    private val encounters: EncounterRepository,
    private val characters: CharacterRepository,
    private val parties: PartyRepository,
) : ScreenModel {
    val encounter: Flow<Encounter> = encounters.getLive(encounterId).right()
    val allNpcsCharacters: Flow<List<Character>> =
        characters.inParty(encounterId.partyId, CharacterType.NPC)

    suspend fun remove() {
        parties.update(encounterId.partyId) { party ->
            if (party.activeCombat?.encounterId == encounterId.encounterId) {
                party.endCombat()
            } else {
                party
            }
        }

        encounters.remove(encounterId)
    }

    suspend fun updateEncounter(encounter: Encounter) {
        encounters.save(encounterId.partyId, encounter)
    }
}
