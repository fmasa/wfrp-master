package cz.muni.fi.rpg.ui.characterCreation

import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.character.Points
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.android.synthetic.main.activity_character_edit.*
import kotlinx.coroutines.*
import javax.inject.Inject

class CharacterEditActivity : PartyScopedActivity(R.layout.activity_character_edit),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    companion object {
        const val EXTRA_CHARACTER_ID = "characterId"
    }

    private lateinit var characterStats: CharacterStatsFormFragment
    private lateinit var characterInfo: CharacterInfoFormFragment

    @Inject
    lateinit var characters: CharacterRepository

    private val characterId by lazy {
        intent.getStringExtra(EXTRA_CHARACTER_ID)
            ?: throw IllegalAccessException("'${EXTRA_CHARACTER_ID}' must be provided")
    }

    override fun onStart() {
        super.onStart()

        characterStats =
            supportFragmentManager.findFragmentById(R.id.characterStats) as CharacterStatsFormFragment
        characterInfo =
            supportFragmentManager.findFragmentById(R.id.characterInfo) as CharacterInfoFormFragment

        launch {
            val character = characters.get(getPartyId(), characterId)

            withContext(Dispatchers.Main) {
                characterStats.setCharacterData(character)
                characterInfo.setCharacterData(character)
            }

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                mainView.visibility = View.VISIBLE
                saveButton.isEnabled = true
            }
        }

        saveButton.setOnClickListener {
            val characterInfoData = characterInfo.submit()
            val characterStatsData = characterStats.submit()

            if (characterInfoData != null && characterStatsData != null) {
                saveButton.isEnabled = false
                launch {
                    updateCharacter(characterInfoData, characterStatsData)
                    finish()
                }
            }
        }
    }

    private suspend fun updateCharacter(
        info: CharacterInfoFormFragment.CharacterInfo,
        statsAndPoints: Pair<Stats, Points>
    ) {
        val character = characters.get(getPartyId(), characterId)

        character.update(
            info.name,
            info.career,
            info.race,
            statsAndPoints.first,
            statsAndPoints.second
        )

        characters.save(getPartyId(), character)
    }
}
