package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.muni.fi.rpg.ui.characterCreation.CharacterBasicInfoForm
import cz.muni.fi.rpg.ui.characterCreation.CharacterCharacteristicsForm
import cz.muni.fi.rpg.ui.characterCreation.PointsPoolForm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID

class CharacterCreationViewModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository
) : ViewModel() {

    suspend fun createCharacter(
        userId: String?,
        info: CharacterBasicInfoForm.Data,
        characteristicsData: CharacterCharacteristicsForm.Data,
        points: PointsPoolForm.Data,
    ): CharacterId {
        val characterId = CharacterId(partyId, userId ?: UUID.randomUUID().toString())

        return withContext(Dispatchers.IO) {
            try {
                Timber.d("Creating character")

                val characteristics = characteristicsData.toValue()

                characters.save(
                    characterId.partyId,
                    Character(
                        id = characterId.id,
                        name = info.name.value,
                        userId = userId,
                        career = info.career.value,
                        socialClass = info.socialClass.value,
                        status = SocialStatus(info.socialTier.value, info.socialStanding.value),
                        race = info.race.value,
                        characteristicsBase = characteristics.base,
                        characteristicsAdvances = characteristics.advances,
                        points = points.toValue(),
                        psychology = info.psychology.value,
                        motivation = info.motivation.value,
                        note = info.note.value,
                    )
                )

                Firebase.analytics.logEvent("create_character") {
                    param("party_id", characterId.partyId.toString())
                    param("character_id", characterId.id)
                }

                characterId
            } catch (e: Throwable) {
                Timber.e(e)
                throw e
            }
        }
    }
}
