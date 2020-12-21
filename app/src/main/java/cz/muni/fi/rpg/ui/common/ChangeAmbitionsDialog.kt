package cz.muni.fi.rpg.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.frantisekmasa.wfrp_master.core.common.SuspendableEntityListener
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.views.TextInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeAmbitionsDialog : DialogFragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_TITLE = "title"
        private const val ARGUMENT_DEFAULTS = "defaults"

        fun newInstance(title: String, defaults: Ambitions) =
            ChangeAmbitionsDialog().apply {
                arguments = bundleOf(
                    ARGUMENT_TITLE to title,
                    ARGUMENT_DEFAULTS to defaults
                )
            }
    }

    private val title: String by stringArgument(ARGUMENT_TITLE)
    private val defaults: Ambitions by parcelableArgument(ARGUMENT_DEFAULTS)

    private var onSaveListener: SuspendableEntityListener<Ambitions>? = null

    private lateinit var form: Form

    fun setOnSaveListener(listener: SuspendableEntityListener<Ambitions>): ChangeAmbitionsDialog {
        onSaveListener = listener

        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()

        val inflater = activity.layoutInflater

        val view = inflater.inflate(R.layout.dialog_change_ambitions, null)

        val shortTermAmbitionInput = view.findViewById<TextInput>(R.id.shortTermAmbitionInput)
        val longTermAmbitionInput = view.findViewById<TextInput>(R.id.longTermAmbitionInput)

        shortTermAmbitionInput.setDefaultValue(defaults.shortTerm)
        longTermAmbitionInput.setDefaultValue(defaults.longTerm)

        form = Form(requireContext()).apply {
            addTextInput(shortTermAmbitionInput).apply {
                setMaxLength(Ambitions.MAX_LENGTH)
            }

            addTextInput(longTermAmbitionInput).apply {
                setMaxLength(Ambitions.MAX_LENGTH)
            }
        }

        val dialog = AlertDialog.Builder(activity, R.style.FormDialog)
            .setView(view)
            .setTitle(title)
            .setPositiveButton(R.string.button_save) { _, _ -> }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener { dialogSubmitted(view) }
        }

        return dialog
    }

    private fun dialogSubmitted(view: View) {

        if (!form.validate()) {
            return
        }

        view.findViewById<View>(R.id.progress).visibility = View.VISIBLE
        view.findViewById<View>(R.id.mainView).visibility = View.GONE

        val ambitions = Ambitions(
            view.findViewById<TextInput>(R.id.shortTermAmbitionInput).getValue(),
            view.findViewById<TextInput>(R.id.longTermAmbitionInput).getValue()
        )

        launch {
            onSaveListener?.let {
                it(ambitions)
            }

            dismiss()
        }
    }
}