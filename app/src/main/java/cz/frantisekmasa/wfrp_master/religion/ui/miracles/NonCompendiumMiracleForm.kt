package cz.frantisekmasa.wfrp_master.religion.ui.miracles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cz.frantisekmasa.wfrp_master.common.core.ui.components.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import java.util.UUID
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle.Companion as CompendiumMiracle

@Composable
internal fun NonCompendiumMiracleForm(
    viewModel: MiraclesViewModel,
    existingMiracle: Miracle?,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumMiracleFormData.fromMiracle(existingMiracle)
    val strings = LocalStrings.current.miracles

    FormDialog(
        title = if (existingMiracle != null) strings.titleNew else strings.titleEdit,
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = viewModel::saveItem,
    ) { validate ->
        TextInput(
            label = strings.labelName,
            value = formData.name,
            validate = validate,
            maxLength = CompendiumMiracle.NAME_MAX_LENGTH
        )

        TextInput(
            label = strings.labelCultName,
            value = formData.cultName,
            validate = validate,
            maxLength = CompendiumMiracle.CULT_NAME_MAX_LENGTH
        )

        TextInput(
            label = strings.labelRange,
            value = formData.range,
            validate = validate,
            maxLength = CompendiumMiracle.RANGE_MAX_LENGTH,
        )

        TextInput(
            label = strings.labelTarget,
            value = formData.target,
            validate = validate,
            maxLength = CompendiumMiracle.TARGET_MAX_LENGTH,
        )

        TextInput(
            label = strings.labelDuration,
            value = formData.duration,
            validate = validate,
            maxLength = CompendiumMiracle.DURATION_MAX_LENGTH,
        )

        TextInput(
            label = strings.labelEffect,
            value = formData.effect,
            validate = validate,
            maxLength = CompendiumMiracle.EFFECT_MAX_LENGTH,
            multiLine = true,
        )
    }
}

private data class NonCompendiumMiracleFormData(
    val id: UUID,
    val name: InputValue,
    val cultName: InputValue,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val effect: InputValue,
) : HydratedFormData<Miracle> {
    companion object {
        @Composable
        fun fromMiracle(miracle: Miracle?) = NonCompendiumMiracleFormData(
            id = remember(miracle) { miracle?.id ?: UUID.randomUUID() },
            name = inputValue(miracle?.name ?: "", Rules.NotBlank()),
            cultName = inputValue(miracle?.cultName ?: ""),
            range = inputValue(miracle?.range ?: ""),
            target = inputValue(miracle?.target ?: ""),
            duration = inputValue(miracle?.duration ?: ""),
            effect = inputValue(miracle?.effect ?: ""),
        )
    }

    override fun toValue() = Miracle(
        id = id,
        compendiumId = null,
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
