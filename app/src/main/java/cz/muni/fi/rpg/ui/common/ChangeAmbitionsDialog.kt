package cz.muni.fi.rpg.ui.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.common.SuspendableEntityListener
import cz.muni.fi.rpg.model.domain.common.Ambitions
import cz.muni.fi.rpg.ui.common.forms.Form
import kotlinx.android.synthetic.main.dialog_change_ambitions.view.*
import kotlinx.android.synthetic.main.dialog_change_ambitions.view.mainView
import kotlinx.android.synthetic.main.dialog_change_ambitions.view.progress
import kotlinx.android.synthetic.main.dialog_change_ambitions.view.shortTermAmbitionInput
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

    private val title: String by lazy {
        requireArguments().getString(ARGUMENT_TITLE) ?: error("Title not set")
    }

    private val defaults: Ambitions by lazy {
        requireArguments().getParcelable<Ambitions>(ARGUMENT_DEFAULTS) ?: error("Defaults not set")
    }

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

        view.shortTermAmbitionInput.setDefaultValue(defaults.shortTerm)
        view.longTermAmbitionInput.setDefaultValue(defaults.longTerm)

        form = Form(requireContext()).apply {
            addTextInput(view.shortTermAmbitionInput).apply {
                setMaxLength(Ambitions.MAX_LENGTH)
            }

            addTextInput(view.longTermAmbitionInput).apply {
                setMaxLength(Ambitions.MAX_LENGTH)
            }
        }

        val dialog = AlertDialog.Builder(activity)
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

        view.progress.visibility = View.VISIBLE
        view.mainView.visibility = View.GONE

        val ambitions = Ambitions(
            view.shortTermAmbitionInput.getValue(),
            view.longTermAmbitionInput.getValue()
        )

        launch {
            onSaveListener?.let {
                it(ambitions)
            }

            dismiss()
        }
    }
}