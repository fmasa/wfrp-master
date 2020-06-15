package cz.muni.fi.rpg.ui.character.skills

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillCharacteristic
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.dialog_skill.view.*
import java.util.UUID

class SkillDialog : DialogFragment() {
    private var onSuccessListener: EntityListener<Skill> = {}

    companion object {
        fun newInstance(existingSkill: Skill?): SkillDialog {
            val fragment = SkillDialog()

            fragment.arguments = bundleOf("skill" to existingSkill)

            return fragment
        }
    }

    val skill: Skill? by lazy { arguments?.getParcelable<Skill>("skill") }

    fun setOnSuccessListener(listener: EntityListener<Skill>): SkillDialog {
        onSuccessListener = listener

        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity();

        val inflater = activity.layoutInflater;

        val view = inflater.inflate(R.layout.dialog_skill, null);

        val characteristicSpinner = view.skillCharacteristic
        val characteristics = SkillCharacteristic.values()
            .map { getString(it.getReadableNameId()) }
            .sorted()

        val form = Form().apply {
            addTextInput(view.skillNameLayout).apply {
                setMaxLength(Skill.NAME_MAX_LENGTH)
                setNotBlank(getString(R.string.error_skill_name_empty))
            }

            addTextInput(view.skillDescriptionLayout).apply {
                setMaxLength(Skill.DESCRIPTION_MAX_LENGTH)
            }

            view.advancesInput.setDefaultValue("1")
            addTextInput(view.advancesInput).apply {
                setNotBlank("Advances must be number greater than 0")
                addLiveRule("Advances must be number greater than 0") {
                    val value = it.toString().toIntOrNull()
                    value != null && value > 0
                }
            }
        }

        characteristicSpinner.setAdapter(
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, characteristics)
        )
        characteristicSpinner.setText(characteristics[0], false)
        characteristicSpinner.setOnClickListener {
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(it.windowToken, 0)
        }

        setDefaults(view)

        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .setTitle(if (skill != null) null else getString(R.string.title_addSkill))
            .setPositiveButton(R.string.button_save) { _, _ -> }
            .setNegativeButton(R.string.button_cancel) { _, _ ->}
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

        view.skillName.setText(skill.name)
        view.skillDescription.setText(skill.description)
        view.skillCharacteristic.setText(getString(skill.characteristic.getReadableNameId()), false)
        view.skillAdvanced.isChecked = skill.advanced
        view.advancesInput.setDefaultValue(skill.advances.toString())
    }

    private fun dialogSubmitted(dialog: AlertDialog, view: View, form: Form) {
        val name = view.skillName.text.toString()
        val description = view.skillDescription.text.toString()

        if (!form.validate()) {
            return
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false

        onSuccessListener(
            Skill(
                this.skill?.id ?: UUID.randomUUID(),
                view.skillAdvanced.isChecked,
                selectedCharacteristic(view),
                name,
                description,
                view.advancesInput.getValue().toInt()
            )
        )
    }

    private fun selectedCharacteristic(view: View): SkillCharacteristic {
        val menuValue = view.skillCharacteristic.text.toString()

        for (item in SkillCharacteristic.values()) {
            if (getString(item.getReadableNameId()) == menuValue) {
                return item
            }
        }

        error("User somehow managed to select something he was not supposed to")
    }
}