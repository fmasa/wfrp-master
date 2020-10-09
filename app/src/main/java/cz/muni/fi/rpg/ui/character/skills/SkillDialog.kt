package cz.muni.fi.rpg.ui.character.skills

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.compendium.common.Characteristic
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.optionalParcelableArgument
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.UUID

class SkillDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        const val ARGUMENT_CHARACTER_ID = "characterId"
        const val ARGUMENT_SKILL = "skill"

        fun newInstance(characterId: CharacterId, existingSkill: Skill?) = SkillDialog().apply {
            arguments = bundleOf(
                ARGUMENT_CHARACTER_ID to characterId,
                ARGUMENT_SKILL to existingSkill
            )
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    val skill: Skill? by optionalParcelableArgument(ARGUMENT_SKILL)

    private val viewModel: SkillsViewModel by viewModel { parametersOf(characterId)}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()

        val inflater = activity.layoutInflater

        val view = inflater.inflate(R.layout.dialog_skill, null)

        addCharacteristicChips(view)
        setDefaults(view)

        val form = Form(requireContext()).apply {
            addTextInput(view.findViewById<TextInput>(R.id.nameInput)).apply {
                setMaxLength(Skill.NAME_MAX_LENGTH, false)
                setNotBlank(getString(R.string.error_skill_name_empty))
            }

            addTextInput(view.findViewById<TextInput>(R.id.descriptionInput)).apply {
                setMaxLength(Skill.DESCRIPTION_MAX_LENGTH, false)
            }

            val advancesInput = view.findViewById<TextInput>(R.id.advancesInput)
            advancesInput.setDefaultValue("1")
            addTextInput(advancesInput).apply {
                setNotBlank("Advances must be number greater than 0")
                addLiveRule("Advances must be number greater than 0") {
                    val value = it.toString().toIntOrNull()
                    value != null && value > 0
                }
            }
        }

        val dialog = AlertDialog.Builder(activity, R.style.FormDialog)
            .setView(view)
            .setTitle(if (skill != null) null else getString(R.string.title_addSkill))
            .setPositiveButton(R.string.button_save) { _, _ -> }
            .setNegativeButton(R.string.button_cancel) { _, _ -> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                dialogSubmitted(dialog, view, form)
            }
        }

        return dialog
    }

    private fun setDefaults(view: View) {
        val skill = this.skill ?: return

        view.findViewById<TextInput>(R.id.nameInput).setDefaultValue(skill.name)
        view.findViewById<TextInput>(R.id.descriptionInput).setDefaultValue(skill.description)
        view.findViewWithTag<Chip>(skill.characteristic).isChecked = true
        view.findViewById<CheckBox>(R.id.skillAdvanced).isChecked = skill.advanced
        view.findViewById<TextInput>(R.id.advancesInput).setDefaultValue(skill.advances.toString())
    }

    private fun dialogSubmitted(dialog: AlertDialog, view: View, form: Form) {
        val name = view.findViewById<TextInput>(R.id.nameInput).getValue()
        val description = view.findViewById<TextInput>(R.id.descriptionInput).getValue()

        if (!form.validate()) {
            return
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false

        val skill = Skill(
            this.skill?.id ?: UUID.randomUUID(),
            view.findViewById<CheckBox>(R.id.skillAdvanced).isChecked,
            selectedCharacteristic(view),
            name,
            description,
            view.findViewById<TextInput>(R.id.advancesInput).getValue().toInt()
        )

        launch {
            viewModel.saveSkill(skill)
            withContext(Dispatchers.Main) { dismiss() }
        }
    }

    private fun addCharacteristicChips(view: View) {
        val group = view.findViewById<ChipGroup>(R.id.skillCharacteristic)
        Characteristic.values().forEach {
            val chip = layoutInflater.inflate(R.layout.item_chip_choice, null, false) as Chip

            chip.setText(it.getShortcutNameId())
            chip.tag = it
            group.addView(chip)
        }

        group
            .findViewWithTag<Chip>(Characteristic.values().first())
            .isChecked = true
    }

    private fun selectedCharacteristic(view: View): Characteristic {
        return view.findViewById<ChipGroup>(R.id.skillCharacteristic).children
            .first { it is Chip && it.isChecked }
            .tag as Characteristic
    }
}