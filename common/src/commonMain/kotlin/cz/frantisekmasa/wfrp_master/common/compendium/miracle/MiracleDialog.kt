package cz.frantisekmasa.wfrp_master.common.compendium.miracle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun MiracleDialog(
    miracle: Miracle?,
    screenModel: MiracleCompendiumScreenModel,
    onDismissRequest: () -> Unit,
) {
    val formData = MiracleFormData.fromItem(miracle)
    val strings = LocalStrings.current.miracles

    CompendiumItemDialog(
        title = if (miracle == null) strings.titleNew else strings.titleEdit,
        formData = formData,
        saver = if (miracle == null) screenModel::createNew else screenModel::update,
        onDismissRequest = onDismissRequest,
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
                helperText = LocalStrings.current.commonUi.markdownSupportedNote,
            )
        }
    }
}

@Stable
private data class MiracleFormData(
    val id: Uuid,
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
            id = remember(item) { item?.id ?: uuid4() },
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
