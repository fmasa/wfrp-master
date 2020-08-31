package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.views.AmbitionsCard
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.CharacterMiscViewModel
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
            val characterAmbitionsCard = view.findViewById<AmbitionsCard>(R.id.characterAmbitionsCard)
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
            view.findViewById<AmbitionsCard>(R.id.partyAmbitionsCard).setValue(party.getAmbitions())
        }
    }

    private fun bindTopBar() {
        viewModel.character.observe(viewLifecycleOwner) { character ->
            val view = requireView()
            view.findViewById<TextView>(R.id.nameValue).text = character.getName()
            view.findViewById<TextView>(R.id.raceValue).setText(character.getRace().getReadableNameId())
            view.findViewById<TextView>(R.id.careerValue).text = character.getCareer()
            view.findViewById<TextView>(R.id.socialClassValue).text = character.getSocialClass()
            view.findViewById<TextView>(R.id.psychologyValue).text = character.getPsychology()
            view.findViewById<TextView>(R.id.motivationValue).text = character.getMotivation()
            view.findViewById<TextView>(R.id.noteValue).text = character.getNote()

            val xpPoints = view.findViewById<Button>(R.id.xpPoints)
            xpPoints.text = getString(R.string.xp_points, character.getPoints().experience)
            xpPoints.setOnClickListener {
                openExperiencePointsDialog(character.getPoints().experience)
            }
        }
    }

    private fun openExperiencePointsDialog(currentXpPoints: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_xp, null, false)

        val xpPointsInput = view.findViewById<TextInput>(R.id.xpPointsInput)
        xpPointsInput.setDefaultValue(currentXpPoints.toString())

        AlertDialog.Builder(requireContext(), R.style.FormDialog)
            .setTitle("Change amount of XP")
            .setView(view)
            .setPositiveButton(R.string.button_save) { _, _ ->
                val xpPoints = xpPointsInput.getValue().toIntOrNull() ?: 0
                launch { viewModel.updateExperiencePoints(xpPoints) }
            }.create()
            .show()
    }
}