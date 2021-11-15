package cz.muni.fi.rpg.model.domain.functions

import android.util.Base64
import com.google.firebase.functions.FirebaseFunctions
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.CharacterAvatarChanger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream

class CloudFunctionCharacterAvatarChanger(
    private val functions: FirebaseFunctions,
) : CharacterAvatarChanger {
    override suspend fun changeAvatar(
        characterId: CharacterId,
        image: InputStream,
    ) {
        withContext(Dispatchers.IO) {
            functions.getHttpsCallable("changeCharacterAvatar")
                .call(mapOf(
                    "partyId" to characterId.partyId.toString(),
                    "characterId" to characterId.id,
                    "imageData" to Base64.encodeToString(image.readBytes(), Base64.DEFAULT)
                )).await()
        }
    }

    override suspend fun removeAvatar(characterId: CharacterId) {
        withContext(Dispatchers.IO) {
            functions.getHttpsCallable("removeCharacterAvatar")
                .call(mapOf(
                    "partyId" to characterId.partyId.toString(),
                    "characterId" to characterId.id,
                )).await()
        }
    }
}