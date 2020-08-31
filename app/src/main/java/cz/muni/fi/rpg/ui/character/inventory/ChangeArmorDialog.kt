package cz.muni.fi.rpg.ui.character.inventory

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.ui.common.forms.Form
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.views.TextInput
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeArmorDialog : DialogFragment(), CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "characterId"
        private const val ARGUMENT_DEFAULTS = "defaults"

        fun newInstance(characterId: CharacterId, defaults: Armor) = ChangeArmorDialog().apply {
            arguments = bundleOf(
                ARGUMENT_CHARACTER_ID to characterId,
                ARGUMENT_DEFAULTS to defaults
            )
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val defaults: Armor by parcelableArgument(ARGUMENT_DEFAULTS)

    private val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        val view = activity.layoutInflater.inflate(R.layout.dialog_change_armor, null)

        Form(activity).apply {
            mapOf(
                view.findViewById<TextInput>(R.id.headInput) to defaults.head,
                view.findViewById<TextInput>(R.id.bodyInput) to defaults.body,
                view.findViewById<TextInput>(R.id.leftArmInput) to defaults.leftArm,
                view.findViewById<TextInput>(R.id.rightArmInput) to defaults.rightArm,
                view.findViewById<TextInput>(R.id.shieldInput) to defaults.shield,
                view.findViewById<TextInput>(R.id.leftLegInput) to defaults.leftLeg,
                view.findViewById<TextInput>(R.id.rightLegInput) to defaults.rightLeg
            ).forEach {
                addTextInput(it.key)
                    .setDefaultValue(it.value.toString())
            }
        }

        val dialog = AlertDialog.Builder(activity, R.style.FormDialog)
            .setTitle(R.string.title_change_armor)
            .setView(view)
            .setPositiveButton(R.string.button_save, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                onConfirm(view)
            }
        }

        return dialog
    }

    private fun onConfirm(view: View) {
        launch {
            viewModel.updateArmor(
                Armor(
                    head = view.findViewById<TextInput>(R.id.headInput).getValue().toIntOrNull() ?: 0,
                    body = view.findViewById<TextInput>(R.id.bodyInput).getValue().toIntOrNull() ?: 0,
                    leftArm = view.findViewById<TextInput>(R.id.leftArmInput).getValue().toIntOrNull() ?: 0,
                    rightArm = view.findViewById<TextInput>(R.id.rightArmInput).getValue().toIntOrNull() ?: 0,
                    shield = view.findViewById<TextInput>(R.id.shieldInput).getValue().toIntOrNull() ?: 0,
                    leftLeg = view.findViewById<TextInput>(R.id.leftLegInput).getValue().toIntOrNull() ?: 0,
                    rightLeg = view.findViewById<TextInput>(R.id.rightLegInput).getValue().toIntOrNull() ?: 0
                )
            )

            withContext(Dispatchers.Main) { dismiss() }
        }
    }
}