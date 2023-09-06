package cz.frantisekmasa.wfrp_master.common.core.firebase.functions

import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterAvatarChanger
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.firebase.functions.CloudFunctions
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CloudFunctionCharacterAvatarChanger(
    private val functions: CloudFunctions,
) : CharacterAvatarChanger {
    override suspend fun changeAvatar(
        characterId: CharacterId,
        image: ByteArray,
    ) {
        withContext(Dispatchers.IO) {
            functions.getHttpsCallable("changeCharacterAvatar")
                .call(
                    mapOf(
                        "partyId" to characterId.partyId.toString(),
                        "characterId" to characterId.id,
                        "imageData" to image.encodeBase64(),
                    )
                )
        }
    }

    override suspend fun removeAvatar(characterId: CharacterId) {
        withContext(Dispatchers.IO) {
            functions.getHttpsCallable("removeCharacterAvatar")
                .call(
                    mapOf(
                        "partyId" to characterId.partyId.toString(),
                        "characterId" to characterId.id,
                    )
                )
        }
    }
}
