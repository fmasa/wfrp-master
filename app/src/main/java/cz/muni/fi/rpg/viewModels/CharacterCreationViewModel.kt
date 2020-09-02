package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.FirebaseAuth
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
    private val characters: CharacterRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    suspend fun createCharacter(
        info: CharacterInfoFormFragment.Data,
        statsData: CharacterStatsFormFragment.CharacteristicsData,
        points: Points
    ): CharacterId {
        val userId = auth.currentUser?.uid ?: error("User not logged in")

        val characterId = CharacterId(partyId, userId)

        withContext(Dispatchers.IO) {

            Timber.d("Creating character")

            characters.save(
                characterId.partyId,
                Character(
                    name = info.name,
                    userId = characterId.userId,
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
                param("character_id", characterId.userId)
            }
        }

        return characterId
    }
}