package cz.frantisekmasa.wfrp_master.common.core.firebase.functions

import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterAvatarChanger
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import dev.gitlive.firebase.functions.FirebaseFunctions
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class CloudFunctionCharacterAvatarChanger(
    private val functions: FirebaseFunctions,
) : CharacterAvatarChanger {
    override suspend fun changeAvatar(
        characterId: CharacterId,
        image: ByteArray,
    ) {
        withContext(Dispatchers.IO) {
            functions.httpsCallable("changeCharacterAvatar")(
                strategy = ChangeAvatarRequest.serializer(),
                data =
                    ChangeAvatarRequest(
                        partyId = characterId.partyId,
                        characterId = characterId.id,
                        imageData = image.encodeBase64(),
                    ),
            )
        }
    }

    @Serializable
    private data class ChangeAvatarRequest(
        val partyId: PartyId,
        val characterId: String,
        val imageData: String,
    )

    override suspend fun removeAvatar(characterId: CharacterId) {
        withContext(Dispatchers.IO) {
            functions.httpsCallable("removeCharacterAvatar")(
                strategy = RemoveAvatarRequest.serializer(),
                data =
                    RemoveAvatarRequest(
                        partyId = characterId.partyId,
                        characterId = characterId.id,
                    ),
            )
        }
    }

    @Serializable
    private data class RemoveAvatarRequest(
        val partyId: PartyId,
        val characterId: String,
    )
}
