package cz.frantisekmasa.wfrp_master.religion.ui.blessings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import java.util.UUID

@Composable
internal fun NonCompendiumBlessingForm(
    viewModel: BlessingsViewModel,
    existingBlessing: Blessing?,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumBlessingFormData.fromBlessing(existingBlessing)

    val strings = LocalStrings.current.blessings

    FormDialog(
        title = if (existingBlessing != null) strings.titleNew else strings.titleEdit,
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = viewModel::saveItem,
    ) { validate ->
        TextInput(
            label = strings.labelName,
            value = formData.name,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.NAME_MAX_LENGTH
        )

        TextInput(
            label = strings.labelRange,
            value = formData.range,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.RANGE_MAX_LENGTH,
        )

        TextInput(
            label = strings.labelTarget,
            value = formData.target,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.TARGET_MAX_LENGTH,
        )

        TextInput(
            label = strings.labelDuration,
            value = formData.duration,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.DURATION_MAX_LENGTH,
        )

        TextInput(
            label = strings.labelEffect,
            value = formData.effect,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.EFFECT_MAX_LENGTH,
            multiLine = true,
        )
    }
}

@Stable
private data class NonCompendiumBlessingFormData(
    val id: UUID,
    val name: InputValue,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val effect: InputValue,
) : HydratedFormData<Blessing> {
    companion object {
        @Composable
        fun fromBlessing(blessing: Blessing?) = NonCompendiumBlessingFormData(
            id = remember(blessing) { blessing?.id ?: UUID.randomUUID() },
            name = inputValue(blessing?.name ?: "", Rules.NotBlank()),
            range = inputValue(blessing?.range ?: ""),
            target = inputValue(blessing?.target ?: ""),
            duration = inputValue(blessing?.duration ?: ""),
            effect = inputValue(blessing?.effect ?: ""),
        )
    }

    override fun toValue() = Blessing(
        id = id,
        compendiumId = null,
        name = name.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        effect = effect.value,
    )

    override fun isValid() =
        listOf(name, range, target, duration, effect).all { it.isValid() }
}
