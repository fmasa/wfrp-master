package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle
import java.util.UUID

@Composable
fun MiracleCompendiumTab(viewModel: CompendiumViewModel, width: Dp) {
    CompendiumTab(
        liveItems = viewModel.miracles,
        emptyUI = {
            val strings = LocalStrings.current.miracles

            EmptyUI(
                text = strings.messages.noMiraclesInCompendium,
                subText = strings.messages.noMiraclesInCompendiumSubtext,
                icon = Resources.Drawable.Miracle
            )
        },
        remover = viewModel::remove,
        saver = viewModel::save,
        dialog = { MiracleDialog(it, viewModel) },
        width = width,
    ) { miracle ->
        ListItem(
            icon = { ItemIcon(Resources.Drawable.Miracle) },
            text = { Text(miracle.name) }
        )
        Divider()
    }
}

@Stable
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
    val strings = LocalStrings.current.miracles

    CompendiumItemDialog(
        title = if (item == null) strings.titleNew else strings.titleEdit,
        formData = formData,
        saver = viewModel::save,
        onDismissRequest = { dialogState.value = DialogState.Closed() }
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = strings.labelName,
                value = formData.name,
                validate = validate,
                maxLength = Miracle.NAME_MAX_LENGTH
            )

            TextInput(
                label = strings.labelCultName,
                value = formData.cultName,
                validate = validate,
                maxLength = Miracle.CULT_NAME_MAX_LENGTH
            )

            TextInput(
                label = strings.labelRange,
                value = formData.range,
                validate = validate,
                maxLength = Miracle.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = strings.labelTarget,
                value = formData.target,
                validate = validate,
                maxLength = Miracle.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = strings.labelDuration,
                value = formData.duration,
                validate = validate,
                maxLength = Miracle.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = strings.labelEffect,
                value = formData.effect,
                validate = validate,
                maxLength = Miracle.EFFECT_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}