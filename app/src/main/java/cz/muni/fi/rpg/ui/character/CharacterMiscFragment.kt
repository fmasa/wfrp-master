package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.viewModels.CharacterMiscViewModel
import kotlinx.android.synthetic.main.dialog_xp.view.*
import kotlinx.android.synthetic.main.fragment_character_misc.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

internal class CharacterMiscFragment : Fragment(R.layout.fragment_character_misc),
    CoroutineScope by CoroutineScope(
        Dispatchers.Default
    ) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterMiscFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val viewModel: CharacterMiscViewModel by viewModel { parametersOf(characterId) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindTopBar()

        viewModel.character.observe(viewLifecycleOwner) { character ->
            characterAmbitionsCard.setValue(character.getAmbitions())

            characterAmbitionsCard.setOnClickListener { _ ->
                ChangeAmbitionsDialog
                    .newInstance(
                        getString(R.string.title_character_ambitions),
                        character.getAmbitions()
                    )
                    .setOnSaveListener { viewModel.updateCharacterAmbitions(it) }
                    .show(childFragmentManager, "ChangeAmbitionsDialog")
            }
        }

        viewModel.party.observe(viewLifecycleOwner) { party ->
            partyAmbitionsCard.setValue(party.getAmbitions())
        }
    }

    private fun bindTopBar() {
        viewModel.character.observe(viewLifecycleOwner) { character ->
            nameValue.text = character.getName()
            raceValue.setText(character.getRace().getReadableNameId())
            careerValue.text = character.getCareer()
            socialClassValue.text = character.getSocialClass()
            psychologyValue.text = character.getPsychology()
            motivationValue.text = character.getMotivation()
            noteValue.text = character.getNote()

            xpPoints.text = getString(R.string.xp_points, character.getPoints().experience)
            xpPoints.setOnClickListener {
                openExperiencePointsDialog(character.getPoints().experience)
            }
        }
    }

    private fun openExperiencePointsDialog(currentXpPoints: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_xp, null, false)

        view.xpPointsInput.setText(currentXpPoints.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Change amount of XP")
            .setView(view)
            .setPositiveButton(R.string.button_save) { _, _ ->
                val xpPoints = view.xpPointsInput.text.toString().toIntOrNull() ?: 0
                launch { viewModel.updateExperiencePoints(xpPoints) }
            }.create()
            .show()
    }
}