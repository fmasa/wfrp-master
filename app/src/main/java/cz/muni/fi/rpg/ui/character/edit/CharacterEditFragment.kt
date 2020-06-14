package cz.muni.fi.rpg.ui.character.edit

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.ui.characterCreation.CharacterInfoFormFragment
import cz.muni.fi.rpg.ui.characterCreation.CharacterStatsData
import cz.muni.fi.rpg.ui.characterCreation.CharacterStatsFormFragment
import cz.muni.fi.rpg.ui.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_character_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CharacterEditFragment(
    private val characters: CharacterRepository
) : BaseFragment(R.layout.fragment_character_edit),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val args: CharacterEditFragmentArgs by navArgs()

    private lateinit var characterStats: CharacterStatsFormFragment
    private lateinit var characterInfo: CharacterInfoFormFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        characterStats =
            childFragmentManager.findFragmentById(R.id.characterStats) as CharacterStatsFormFragment
        characterInfo =
            childFragmentManager.findFragmentById(R.id.characterInfo) as CharacterInfoFormFragment

        setSubtitle(getString(R.string.subtitle_edit_character))

        launch {
            val character = characters.get(args.characterId)

            withContext(Dispatchers.Main) {
                setTitle(character.getName())
                characterStats.setCharacterData(character)
                characterInfo.setCharacterData(character)
            }

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                mainView.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.character_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionSave) {
            val characterInfoData = characterInfo.submit()
            val characterStatsData = characterStats.submit()

            if (characterInfoData != null && characterStatsData != null) {
                item.isEnabled = false
                launch {
                    updateCharacter(characterInfoData, characterStatsData)
                    withContext(Dispatchers.Main) {
                        findNavController().popBackStack()
                    }
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private suspend fun updateCharacter(
        info: CharacterInfoFormFragment.CharacterInfo,
        characterStatsData: CharacterStatsData
    ) {
        val character = characters.get(args.characterId)
        val points = character.getPoints()

        character.update(
            info.name,
            info.career,
            info.socialClass,
            info.race,
            characterStatsData.stats,
            points.updateMaxWounds(characterStatsData.maxWounds)
        )

        characters.save(args.characterId.partyId, character)
    }
}