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
import cz.muni.fi.rpg.ui.characterCreation.CharacterStatsFormFragment
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.fragment_character_edit.*
import kotlinx.android.synthetic.main.fragment_character_edit.maxWoundsInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class CharacterEditFragment(
    private val characters: CharacterRepository
) : PartyScopedFragment(R.layout.fragment_character_edit),
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
                hardyTalentCheckbox.isChecked = character.hasHardyTalent()
            }

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                mainView.visibility = View.VISIBLE
            }
        }
    }

    override fun getPartyId(): UUID = args.characterId.partyId

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.form_fragment_menu, menu)
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
                updateCharacter(
                    info,
                    statsData,
                    maxWoundsInput.getValue().toInt(),
                    hardyTalentCheckbox.isChecked
                )
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
        maxWounds: Int,
        hardyTalent: Boolean
    ) {
        val character = characters.get(args.characterId)

        character.update(
            name = info.name,
            career = info.career,
            socialClass = info.socialClass,
            race = info.race,
            stats = statsData.stats,
            maxStats = statsData.maxStats,
            maxWounds = maxWounds,
            psychology = info.psychology,
            motivation = info.motivation,
            note = info.note,
            hardyTalent = hardyTalent
        )

        characters.save(args.characterId.partyId, character)
    }
}