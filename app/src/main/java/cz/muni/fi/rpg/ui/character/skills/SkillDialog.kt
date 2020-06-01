package cz.muni.fi.rpg.ui.character.skills

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillCharacteristic
import kotlinx.android.synthetic.main.dialog_skill.view.*
import java.util.UUID

typealias OnSuccessListener = (Skill) -> Unit

class SkillDialog : DialogFragment() {
    private var userAttemptedSubmit: Boolean = false
    private var onSuccessListener: OnSuccessListener = {}

    companion object {
        fun newInstance(existingSkill: Skill?): SkillDialog {
            val fragment = SkillDialog()

            fragment.arguments = bundleOf("skill" to existingSkill)

            return fragment
        }
    }

    val skill: Skill? by lazy { arguments?.getParcelable<Skill>("skill") }

    fun setOnSuccessListener(listener: OnSuccessListener): SkillDialog {
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
                dialogSubmitted(dialog, view)
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
        view.skillMastery.check(when (skill.mastery) {
            1 -> R.id.skillMasteryLevel1
            2 -> R.id.skillMasteryLevel2
            3 -> R.id.skillMasteryLevel3
            else -> error("Mastery must be value between 1 and 3, ${skill.mastery} given")
        })
    }

    private fun dialogSubmitted(dialog: AlertDialog, view: View) {
        val name = view.skillName.text.toString()
        val description = view.skillDescription.text.toString()

        if (! userAttemptedSubmit) {
            view.skillName.addTextChangedListener { nameInputChanged(view) }
            nameInputChanged(view)

            userAttemptedSubmit = true
        }

        if (name.isEmpty()) {
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
                selectedMastery(view)
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

    private fun selectedMastery(view: View): Int {
        return when (view.skillMastery.checkedRadioButtonId) {
            R.id.skillMasteryLevel1 -> 1
            R.id.skillMasteryLevel2 -> 2
            R.id.skillMasteryLevel3 -> 3
            else -> error("Unknown radio button for mastery")
        }
    }

    private fun nameInputChanged(view: View) {
        view.skillNameLayout.error = if (view.skillName.text.toString().isEmpty())
            getString(R.string.error_skill_name_empty)
        else null
    }
}