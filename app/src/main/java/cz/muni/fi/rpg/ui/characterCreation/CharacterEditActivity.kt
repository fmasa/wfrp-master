package cz.muni.fi.rpg.ui.characterCreation

import android.content.Context
import android.content.Intent
import android.view.View
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
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
        const val EXTRA_USER_ID = "characterId"

        fun start(characterId: CharacterId, packageContext: Context) {
            val intent = Intent(packageContext, CharacterEditActivity::class.java)
            intent.putExtra(EXTRA_PARTY_ID, characterId.partyId.toString())
            intent.putExtra(EXTRA_USER_ID, characterId.userId)

            packageContext.startActivity(intent)
        }
    }

    private lateinit var characterStats: CharacterStatsFormFragment
    private lateinit var characterInfo: CharacterInfoFormFragment

    @Inject
    lateinit var characters: CharacterRepository

    private val characterId by lazy {
        CharacterId(
            getPartyId(),
            intent.getStringExtra(EXTRA_USER_ID)
                ?: throw IllegalAccessException("'${EXTRA_USER_ID}' must be provided")
        )
    }

    override fun onStart() {
        super.onStart()

        characterStats =
            supportFragmentManager.findFragmentById(R.id.characterStats) as CharacterStatsFormFragment
        characterInfo =
            supportFragmentManager.findFragmentById(R.id.characterInfo) as CharacterInfoFormFragment

        supportActionBar?.subtitle = getString(R.string.subtitle_edit_character)

        launch {
            val character = characters.get(characterId.partyId, characterId.userId)

            withContext(Dispatchers.Main) {
                supportActionBar?.title = character.getName()
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
        val character = characters.get(characterId.partyId, characterId.userId)

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
