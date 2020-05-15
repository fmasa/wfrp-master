package cz.muni.fi.rpg.ui.character.skills

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.model.domain.skills.SkillCharacteristic
import kotlinx.android.synthetic.main.dialog_skill.view.*
import java.util.UUID

typealias OnSuccessListener = (Skill) -> Unit

class SkillDialog : DialogFragment() {
    private var userAttemptedSubmit: Boolean = false
    private var onSuccessListener: OnSuccessListener = {}

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

        val dialog = AlertDialog.Builder(activity)
            .setTitle(R.string.title_addSkill)
            .setView(view)
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
                UUID.randomUUID(),
                view.skillAdvanced.isChecked,
                selectedCharacteristic(view),
                name,
                description
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

    private fun nameInputChanged(view: View) {
        view.skillNameLayout.error = if (view.skillName.text.toString().isEmpty())
            getString(R.string.error_skill_name_empty)
        else null
    }
}