package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.compendium.R
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SpellCompendiumTab(viewModel: CompendiumViewModel, width: Dp) {
    val coroutineScope = rememberCoroutineScope()

    CompendiumTab(
        liveItems = viewModel.spells,
        emptyUI = {
            EmptyUI(
                textId = R.string.no_spells_in_compendium,
                subTextId = R.string.no_spells_in_compendium_sub_text,
                drawableResourceId = R.drawable.ic_spells
            )
        },
        onRemove = { coroutineScope.launch(Dispatchers.IO) { viewModel.remove(it) } },
        dialog = { SpellDialog(it, viewModel) },
        width = width,
    ) { spell ->
        ListItem(
            icon = { ItemIcon(R.drawable.ic_spells) },
            text = { Text(spell.name) }
        )
        Divider()
    }
}

private data class SpellFormData(
    val id: UUID,
    val name: InputValue,
    val lore: InputValue,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val castingNumber: InputValue,
    val effect: InputValue,
) : CompendiumItemFormData<Spell> {
    companion object {
        @Composable
        fun fromItem(item: Spell?) = SpellFormData(
            id = remember(item) { item?.id ?: UUID.randomUUID() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            lore = inputValue(item?.lore ?: ""),
            range = inputValue(item?.range ?: ""),
            target = inputValue(item?.target ?: ""),
            duration = inputValue(item?.duration ?: ""),
            castingNumber = inputValue(item?.castingNumber?.toString() ?: "0", Rules.PositiveInteger()),
            effect = inputValue(item?.effect ?: ""),
        )
    }

    override fun toValue() = Spell(
        id = id,
        name = name.value,
        lore = lore.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        castingNumber = castingNumber.value.toInt(),
        effect = effect.value,
    )

    override fun isValid() =
        listOf(name, lore, range, target, duration, castingNumber, effect).all { it.isValid() }
}

@Composable
private fun SpellDialog(
    dialogState: MutableState<DialogState<Spell?>>,
    viewModel: CompendiumViewModel
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val item = dialogStateValue.item
    val formData = SpellFormData.fromItem(item)

    CompendiumItemDialog(
        title = stringResource(if (item == null) R.string.title_spell_add else R.string.title_spell_edit),
        formData = formData,
        saver = viewModel::save,
        onDismissRequest = { dialogState.value = DialogState.Closed() }
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name,
                validate = validate,
                maxLength = Spell.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_spell_lore),
                value = formData.lore,
                validate = validate,
                maxLength = Spell.LORE_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_range),
                value = formData.range,
                validate = validate,
                maxLength = Spell.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_target),
                value = formData.target,
                validate = validate,
                maxLength = Spell.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_duration),
                value = formData.duration,
                validate = validate,
                maxLength = Spell.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_casting_number),
                value = formData.castingNumber,
                keyboardType = KeyboardType.Number,
                validate = validate,
                maxLength = 2,
            )

            TextInput(
                label = stringResource(R.string.label_effect),
                value = formData.effect,
                validate = validate,
                maxLength = Spell.EFFECT_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}
