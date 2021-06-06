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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.compendium.R
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle
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
import java.util.*

@Composable
fun MiracleCompendiumTab(viewModel: CompendiumViewModel, width: Dp) {
    val coroutineScope = rememberCoroutineScope()

    CompendiumTab(
        liveItems = viewModel.miracles,
        emptyUI = {
            EmptyUI(
                textId = R.string.no_miracles_in_compendium,
                subTextId = R.string.no_miracles_in_compendium_sub_text,
                drawableResourceId = R.drawable.ic_pray
            )
        },
        onRemove = { coroutineScope.launch(Dispatchers.IO) { viewModel.remove(it) } },
        dialog = { MiracleDialog(it, viewModel) },
        width = width,
    ) { miracle ->
        ListItem(
            icon = { ItemIcon(R.drawable.ic_pray) },
            text = { Text(miracle.name) }
        )
        Divider()
    }
}

private data class MiracleFormData(
    val id: UUID,
    val name: InputValue,
    val cultName: InputValue,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val effect: InputValue,
) : CompendiumItemFormData<Miracle> {
    companion object {
        @Composable
        fun fromItem(item: Miracle?) = MiracleFormData(
            id = remember(item) { item?.id ?: UUID.randomUUID() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            cultName = inputValue(item?.cultName ?: ""),
            range = inputValue(item?.range ?: ""),
            target = inputValue(item?.target ?: ""),
            duration = inputValue(item?.duration ?: ""),
            effect = inputValue(item?.effect ?: ""),
        )
    }

    override fun toValue() = Miracle(
        id = id,
        name = name.value,
        cultName = cultName.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        effect = effect.value,
    )

    override fun isValid() =
        listOf(name, cultName, range, target, duration, effect).all { it.isValid() }
}

@Composable
private fun MiracleDialog(
    dialogState: MutableState<DialogState<Miracle?>>,
    viewModel: CompendiumViewModel
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val item = dialogStateValue.item
    val formData = MiracleFormData.fromItem(item)

    CompendiumItemDialog(
        title = stringResource(if (item == null) R.string.title_miracle_add else R.string.title_miracle_edit),
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
                maxLength = Miracle.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_miracle_cult_name),
                value = formData.cultName,
                validate = validate,
                maxLength = Miracle.CULT_NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_range),
                value = formData.range,
                validate = validate,
                maxLength = Miracle.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_target),
                value = formData.target,
                validate = validate,
                maxLength = Miracle.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_duration),
                value = formData.duration,
                validate = validate,
                maxLength = Miracle.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_effect),
                value = formData.effect,
                validate = validate,
                maxLength = Miracle.EFFECT_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}
