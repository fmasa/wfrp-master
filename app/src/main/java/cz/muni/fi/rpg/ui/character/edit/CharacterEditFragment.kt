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
import cz.muni.fi.rpg.model.domain.character.Stats
import cz.muni.fi.rpg.ui.characterCreation.CharacterInfoFormFragment
import cz.muni.fi.rpg.ui.characterCreation.CharacterStatsFormFragment
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.fragment_character_edit.*
import kotlinx.android.synthetic.main.fragment_character_edit.maxWoundsInput
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
    private lateinit var form: Form

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        characterStats =
            childFragmentManager.findFragmentById(R.id.characterStats) as CharacterStatsFormFragment
        characterInfo =
            childFragmentManager.findFragmentById(R.id.characterInfo) as CharacterInfoFormFragment

        setSubtitle(getString(R.string.subtitle_edit_character))

        form = Form(requireContext()).apply {
            addTextInput(maxWoundsInput).apply {
                addLiveRule(R.string.error_required) { !it.isNullOrBlank() }
                addLiveRule(R.string.error_value_over_100) { it.toString().toInt() <= 100 }
                addLiveRule(R.string.error_value_is_0) { it.toString().toInt() > 0 }
            }
        }

        launch {
            val character = characters.get(args.characterId)

            withContext(Dispatchers.Main) {
                setTitle(character.getName())
                characterStats.setCharacterData(character)
                characterInfo.setCharacterData(character)
                maxWoundsInput.setDefaultValue(character.getPoints().maxWounds.toString())
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
        if (item.itemId != R.id.actionSave) {
            return super.onOptionsItemSelected(item)
        }

        val info = characterInfo.submit()
        val statsData = characterStats.submit()

        if (info != null && statsData != null && form.validate()) {
            item.isEnabled = false
            launch {
                updateCharacter(info, statsData, maxWoundsInput.getValue().toInt())
                withContext(Dispatchers.Main) {
                    findNavController().popBackStack()
                }
            }
        }

        return false
    }

    private suspend fun updateCharacter(
        info: CharacterInfoFormFragment.Data,
        statsData: CharacterStatsFormFragment.Data,
        maxWounds: Int
    ) {
        val character = characters.get(args.characterId)
        val points = character.getPoints()

        character.update(
            info.name,
            info.career,
            info.socialClass,
            info.race,
            statsData.stats,
            statsData.maxStats,
            points.updateMaxWounds(maxWounds)
        )

        characters.save(args.characterId.partyId, character)
    }
}