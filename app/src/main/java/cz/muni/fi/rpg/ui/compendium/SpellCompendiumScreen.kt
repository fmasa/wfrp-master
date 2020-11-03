package cz.muni.fi.rpg.ui.compendium

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraintsScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.compendium.Spell
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.composables.dialog.DialogState
import cz.muni.fi.rpg.viewModels.CompendiumViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalLayout
@Composable
fun WithConstraintsScope.SpellCompendiumTab(viewModel: CompendiumViewModel) {
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
        width = maxWidth,
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
    val name: MutableState<String>,
    val range: MutableState<String>,
    val target: MutableState<String>,
    val duration: MutableState<String>,
    val castingNumber: MutableState<String>,
    val effect: MutableState<String>,
) : CompendiumItemFormData<Spell> {
    companion object {
        @Composable
        fun fromState(state: DialogState.Opened<Spell?>) = SpellFormData(
            id = remember(state) { state.item?.id ?: UUID.randomUUID() },
            name = savedInstanceState(state) { state.item?.name ?: "" },
            range = savedInstanceState(state) { state.item?.range ?: "" },
            target = savedInstanceState(state) { state.item?.target ?: "" },
            duration = savedInstanceState(state) { state.item?.duration ?: "" },
            castingNumber = savedInstanceState(state) {
                state.item?.castingNumber?.toString() ?: ""
            },
            effect = savedInstanceState(state) { state.item?.effect ?: "" },
        )
    }

    override fun toItem() = Spell(
        id = id,
        name = name.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        castingNumber = castingNumber.value.toInt(),
        effect = effect.value,
    )

    override fun isValid() =
        name.value.isNotBlank() &&
                name.value.length <= Spell.NAME_MAX_LENGTH &&
                range.value.length <= Spell.RANGE_MAX_LENGTH &&
                target.value.length <= Spell.TARGET_MAX_LENGTH &&
                duration.value.length <= Spell.DURATION_MAX_LENGTH &&
                (castingNumber.value.toIntOrNull() ?: 0) >= 0 &&
                effect.value.length <= Spell.EFFECT_MAX_LENGTH
}

@ExperimentalLayout
@Composable
private fun SpellDialog(
    dialogState: MutableState<DialogState<Spell?>>,
    viewModel: CompendiumViewModel
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val formData = SpellFormData.fromState(dialogStateValue)

    CompendiumItemDialog(
        title = stringResource(
            if (dialogStateValue.item == null) R.string.title_spell_add else R.string.title_spell_edit
        ),
        formData = formData,
        saver = viewModel::save,
        onDismissRequest = { dialogState.value = DialogState.Closed() }
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(BodyPadding),
        ) {
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name.value,
                onValueChange = { formData.name.value = it },
                validate = validate,
                maxLength = Spell.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_spell_range),
                value = formData.range.value,
                onValueChange = { formData.range.value = it },
                validate = validate,
                maxLength = Spell.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_target),
                value = formData.target.value,
                onValueChange = { formData.target.value = it },
                validate = validate,
                maxLength = Spell.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_duration),
                value = formData.duration.value,
                onValueChange = { formData.duration.value = it },
                validate = validate,
                maxLength = Spell.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_casting_number),
                value = formData.castingNumber.value,
                keyboardType = KeyboardType.Number,
                onValueChange = { formData.castingNumber.value = it },
                validate = validate,
                maxLength = 2,
            )

            TextInput(
                label = stringResource(R.string.label_spell_effect),
                value = formData.effect.value,
                onValueChange = { formData.effect.value = it },
                validate = validate,
                maxLength = Spell.EFFECT_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}
