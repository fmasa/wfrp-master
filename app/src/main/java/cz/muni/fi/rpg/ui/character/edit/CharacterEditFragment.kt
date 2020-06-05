package cz.muni.fi.rpg.ui.character.edit

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.ui.characterCreation.CharacterInfoFormFragment
import cz.muni.fi.rpg.ui.characterCreation.CharacterStatsData
import cz.muni.fi.rpg.ui.characterCreation.CharacterStatsFormFragment
import cz.muni.fi.rpg.ui.common.BaseFragment
import kotlinx.android.synthetic.main.activity_character_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterEditFragment(
    private val characters: CharacterRepository
) : BaseFragment(R.layout.activity_character_edit),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val args: CharacterEditFragmentArgs by navArgs()

    private lateinit var characterStats: CharacterStatsFormFragment
    private lateinit var characterInfo: CharacterInfoFormFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        characterStats =
            childFragmentManager.findFragmentById(R.id.characterStats) as CharacterStatsFormFragment
        characterInfo =
            childFragmentManager.findFragmentById(R.id.characterInfo) as CharacterInfoFormFragment

        setSubtitle(getString(R.string.subtitle_edit_character))

        launch {
            val character = characters.get(args.characterId.partyId, args.characterId.userId)

            withContext(Dispatchers.Main) {
                setTitle(character.getName())
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
                    withContext(Dispatchers.Main) {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private suspend fun updateCharacter(
        info: CharacterInfoFormFragment.CharacterInfo,
        characterStatsData: CharacterStatsData
    ) {
        val character = characters.get(args.characterId.partyId, args.characterId.userId)
        val points = character.getPoints()

        character.update(
            info.name,
            info.career,
            info.race,
            characterStatsData.stats,
            points.updateFate(characterStatsData.fatePoints)
                .updateMaxWounds(characterStatsData.maxWounds)
        )

        characters.save(args.characterId.partyId, character)
    }
}