package cz.frantisekmasa.wfrp_master.common.npcs

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.LocalCharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName
import dev.gitlive.firebase.functions.FirebaseFunctions
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

class NpcsScreenModel(
    val partyId: PartyId,
    private val functions: FirebaseFunctions,
    private val characters: CharacterRepository,
) : ScreenModel {
    val npcs: Flow<List<NpcList.Item>> =
        characters.inParty(partyId, CharacterType.NPC)
            .map { characters ->
                characters.map {
                    NpcList.Item(
                        id = it.id,
                        name = it.name,
                        avatarUrl = it.avatarUrl,
                    )
                }
            }

    suspend fun archiveNpc(npcId: LocalCharacterId) {
        try {
            val npc = characters.get(CharacterId(partyId, npcId)).archive()
            characters.save(partyId, npc.archive())
        } catch (e: CharacterNotFound) {
            Napier.w("Character $npcId not found in party $partyId")
        }
    }

    suspend fun duplicate(
        npcId: LocalCharacterId,
        originalName: String,
    ) {
        functions.httpsCallable("duplicateCharacter")(
            DuplicateCharacterRequest.serializer(),
            DuplicateCharacterRequest(
                partyId = partyId,
                characterId = npcId,
                newName = duplicateName(originalName, Character.NAME_MAX_LENGTH),
            ),
        )
    }

    @Serializable
    private data class DuplicateCharacterRequest(
        val partyId: PartyId,
        val characterId: String,
        val newName: String,
    )
}
