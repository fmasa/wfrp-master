package cz.frantisekmasa.wfrp_master.common.npcs

import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName
import cz.frantisekmasa.wfrp_master.common.firebase.functions.CloudFunctions
import kotlinx.coroutines.flow.Flow

class NpcsScreenModel(
    private val partyId: PartyId,
    private val functions: CloudFunctions,
    private val characters: CharacterRepository,
) : ScreenModel {

    val npcs: Flow<List<Character>> = characters.inParty(partyId, CharacterType.NPC)

    suspend fun archiveNpc(npc: Character) {
        characters.save(partyId, npc.archive())
    }

    suspend fun duplicate(npc: Character) {
        functions.getHttpsCallable("duplicateCharacter")
            .call(
                mapOf(
                    "partyId" to partyId.toString(),
                    "characterId" to npc.id,
                    "newName" to duplicateName(npc.name)
                )
            )
    }
}
