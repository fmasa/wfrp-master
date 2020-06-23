package cz.muni.fi.rpg.ui.character.skills.talents

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.EntityListener
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.parcelableArgument
import kotlinx.android.synthetic.main.dialog_talent.view.*
import java.util.*

class TalentDialog : DialogFragment() {
    private var onSuccessListener: EntityListener<Talent> = {}

    companion object {
        fun newInstance(existingTalent: Talent?): TalentDialog {
            val fragment = TalentDialog()

            fragment.arguments = bundleOf("talent" to existingTalent)

            return fragment
        }
    }

    val talent: Talent? by parcelableArgument("talent")

    fun setOnSuccessListener(listener: EntityListener<Talent>): TalentDialog {
        onSuccessListener = listener

        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()

        val inflater = activity.layoutInflater

        val view = inflater.inflate(R.layout.dialog_talent, null)

        val form = Form(requireContext()).apply {
            addTextInput(view.talentNameInput).apply {
                setMaxLength(Talent.NAME_MAX_LENGTH)
                setNotBlank(getString(R.string.error_talent_name_empty))
            }

            addTextInput(view.talentDescriptionInput).apply {
                setMaxLength(Talent.DESCRIPTION_MAX_LENGTH)
            }

            addTextInput(view.talentTakenInput).apply {
                setNotBlank("Taken must be number greater than 0")
                addLiveRule("Taken must be number greater than 0") {
                    val value = it.toString().toIntOrNull()
                    value != null && value > 0
                }
            }
        }

        setDefaults(view)

        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .setTitle(if (talent != null) null else getString(R.string.title_talent_add))
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
        view.talentTakenInput.setDefaultValue("1")

        val talent = this.talent ?: return

        view.talentNameInput.setDefaultValue(talent.name)
        view.talentDescriptionInput.setDefaultValue(talent.description)
        view.talentTakenInput.setDefaultValue(talent.taken.toString())
    }

    private fun dialogSubmitted(dialog: AlertDialog, view: View, form: Form) {
        val name = view.talentNameInput.getValue()
        val description = view.talentDescriptionInput.getValue()
        val taken = view.talentTakenInput.getValue().toInt()

        if (!form.validate()) {
            return
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false

        onSuccessListener(Talent(this.talent?.id ?: UUID.randomUUID(), name, description, taken))
    }
}