package cz.frantisekmasa.wfrp_master.common.npcs

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName
import dev.gitlive.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

class NpcsScreenModel(
    private val partyId: PartyId,
    private val functions: FirebaseFunctions,
    private val characters: CharacterRepository,
) : ScreenModel {

    val npcs: Flow<List<Character>> = characters.inParty(partyId, CharacterType.NPC)

    suspend fun archiveNpc(npc: Character) {
        characters.save(partyId, npc.archive())
    }

    suspend fun duplicate(npc: Character) {
        functions.httpsCallable("duplicateCharacter")(
            DuplicateCharacterRequest.serializer(),
            DuplicateCharacterRequest(
                partyId = partyId,
                characterId = npc.id,
                newName = duplicateName(npc.name)
            )
        )
    }

    @Serializable
    private data class DuplicateCharacterRequest(
        val partyId: PartyId,
        val characterId: String,
        val newName: String,
    )
}
