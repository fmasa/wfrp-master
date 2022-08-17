package cz.frantisekmasa.wfrp_master.common.npcs

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import kotlinx.coroutines.flow.Flow

class NpcsScreenModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository,
): ScreenModel {

    val npcs: Flow<List<Character>> = characters.inParty(partyId, CharacterType.NPC)

    suspend fun archiveNpc(npc: Character) {
        characters.save(partyId, npc.archive())
    }
}