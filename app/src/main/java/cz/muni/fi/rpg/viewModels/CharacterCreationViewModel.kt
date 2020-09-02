package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.character.Points
import cz.muni.fi.rpg.ui.characterCreation.CharacterInfoFormFragment
import cz.muni.fi.rpg.ui.characterCreation.CharacterStatsFormFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class CharacterCreationViewModel(
    private val partyId: UUID,
    private val characters: CharacterRepository
) : ViewModel() {

    suspend fun createCharacter(
        userId: String?,
        info: CharacterInfoFormFragment.Data,
        statsData: CharacterStatsFormFragment.CharacteristicsData,
        points: Points
    ): CharacterId {
        val characterId = CharacterId(partyId, userId ?: UUID.randomUUID().toString())

        withContext(Dispatchers.IO) {

            Timber.d("Creating character")

            characters.save(
                characterId.partyId,
                Character(
                    id = characterId.id,
                    name = info.name,
                    userId = userId,
                    career = info.career,
                    socialClass = info.socialClass,
                    race = info.race,
                    characteristicsBase = statsData.base,
                    characteristicsAdvances = statsData.advances,
                    points = points,
                    psychology = info.psychology,
                    motivation = info.motivation,
                    note = info.note
                )
            )

            Firebase.analytics.logEvent("create_character") {
                param("party_id", characterId.partyId.toString())
                param("character_id", characterId.id)
            }
        }

        return characterId
    }
}