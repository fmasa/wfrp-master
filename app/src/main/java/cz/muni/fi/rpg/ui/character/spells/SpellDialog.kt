package cz.muni.fi.rpg.ui.character.spells

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.optionalParcelableArgument
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class SpellDialog : DialogFragment(),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        fun newInstance(characterId: CharacterId, existingSpell: Spell?): SpellDialog {
            val fragment = SpellDialog()

            fragment.arguments = bundleOf(
                "characterId" to characterId,
                "spell" to existingSpell
            )

            return fragment
        }
    }

    private val existingSpell: Spell? by optionalParcelableArgument("spell")
    private val characterId: CharacterId by parcelableArgument("characterId")
    private val viewModel: SpellsViewModel by viewModel { parametersOf(characterId) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()

        val inflater = activity.layoutInflater

        val view = inflater.inflate(R.layout.dialog_spell, null)

        val form = Form(requireContext()).apply {
            addTextInput(view.findViewById<TextInput>(R.id.spellNameInput)).apply {
                setMaxLength(Spell.NAME_MAX_LENGTH, false)
                setNotBlank(getString(R.string.error_cannot_be_empty))
            }

            addTextInput(view.findViewById<TextInput>(R.id.spellDurationInput)).apply {
                setMaxLength(Spell.DURATION_MAX_LENGTH, false)
            }

            addTextInput(view.findViewById<TextInput>(R.id.spellRangeInput)).apply {
                setMaxLength(Spell.RANGE_MAX_LENGTH, false)
            }

            addTextInput(view.findViewById<TextInput>(R.id.spellTargetInput)).apply {
                setMaxLength(Spell.TARGET_MAX_LENGTH, false)
            }

            addTextInput(view.findViewById<TextInput>(R.id.spellCastingNumberInput)).apply {
                setNotBlank("CN must not be empty")
            }

            addTextInput(view.findViewById<TextInput>(R.id.spellEffectInput)).apply {
                setMaxLength(Spell.EFFECT_MAX_LENGTH, false)
            }
        }

        setDefaults(view)

        val dialog = AlertDialog.Builder(activity, R.style.FormDialog)
            .setView(view)
            .setTitle(if (existingSpell != null) null else getString(R.string.title_spell_add))
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
        val spell = this.existingSpell ?: return

        view.findViewById<TextInput>(R.id.spellNameInput).setDefaultValue(spell.name)
        view.findViewById<TextInput>(R.id.spellDurationInput).setDefaultValue(spell.duration)
        view.findViewById<TextInput>(R.id.spellRangeInput).setDefaultValue(spell.range)
        view.findViewById<TextInput>(R.id.spellTargetInput).setDefaultValue(spell.target)
        view.findViewById<TextInput>(R.id.spellCastingNumberInput).setDefaultValue(spell.castingNumber.toString())
        view.findViewById<TextInput>(R.id.spellEffectInput).setDefaultValue(spell.effect)
    }

    private fun dialogSubmitted(dialog: AlertDialog, view: View, form: Form) {
        if (!form.validate()) {
            return
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false

        val spell = Spell(
            id = existingSpell?.id ?: UUID.randomUUID(),
            name = view.findViewById<TextInput>(R.id.spellNameInput).getValue(),
            range = view.findViewById<TextInput>(R.id.spellRangeInput).getValue(),
            target = view.findViewById<TextInput>(R.id.spellTargetInput).getValue(),
            castingNumber = view.findViewById<TextInput>(R.id.spellCastingNumberInput).getValue().toInt(),
            duration = view.findViewById<TextInput>(R.id.spellDurationInput).getValue(),
            effect = view.findViewById<TextInput>(R.id.spellEffectInput).getValue()
        )

        launch {
            viewModel.saveSpell(spell)

            withContext(Dispatchers.Main) { toast(R.string.message_spell_saved) }
            dismiss()
        }

    }
}