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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.muni.fi.rpg.R
import java.util.UUID

@Composable
fun BlessingCompendiumTab(viewModel: CompendiumViewModel, width: Dp) {
    CompendiumTab(
        liveItems = viewModel.blessings,
        emptyUI = {
            EmptyUI(
                textId = R.string.no_blessings_in_compendium,
                subTextId = R.string.no_blessings_in_compendium_sub_text,
                drawableResourceId = R.drawable.ic_pray
            )
        },
        remover = viewModel::remove,
        saver = viewModel::save,
        dialog = { BlessingDialog(it, viewModel) },
        width = width,
    ) { blessing ->
        ListItem(
            icon = { ItemIcon(R.drawable.ic_pray) },
            text = { Text(blessing.name) }
        )
        Divider()
    }
}

private data class BlessingFormData(
    val id: UUID,
    val name: InputValue,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val effect: InputValue,
) : CompendiumItemFormData<Blessing> {
    companion object {
        @Composable
        fun fromItem(item: Blessing?) = BlessingFormData(
            id = remember(item) { item?.id ?: UUID.randomUUID() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            range = inputValue(item?.range ?: ""),
            target = inputValue(item?.target ?: ""),
            duration = inputValue(item?.duration ?: ""),
            effect = inputValue(item?.effect ?: ""),
        )
    }

    override fun toValue() = Blessing(
        id = id,
        name = name.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        effect = effect.value,
    )

    override fun isValid() =
        listOf(name, range, target, duration, effect).all { it.isValid() }
}

@Composable
private fun BlessingDialog(
    dialogState: MutableState<DialogState<Blessing?>>,
    viewModel: CompendiumViewModel
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val item = dialogStateValue.item
    val formData = BlessingFormData.fromItem(item)

    CompendiumItemDialog(
        title = stringResource(if (item == null) R.string.title_blessing_new else R.string.title_blessing_edit),
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
                maxLength = Blessing.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_range),
                value = formData.range,
                validate = validate,
                maxLength = Blessing.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_target),
                value = formData.target,
                validate = validate,
                maxLength = Blessing.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_duration),
                value = formData.duration,
                validate = validate,
                maxLength = Blessing.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_effect),
                value = formData.effect,
                validate = validate,
                maxLength = Blessing.EFFECT_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}
