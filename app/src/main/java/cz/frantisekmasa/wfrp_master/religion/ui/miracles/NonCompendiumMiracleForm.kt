package cz.frantisekmasa.wfrp_master.religion.ui.miracles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.common.core.ui.components.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import cz.muni.fi.rpg.R
import java.util.UUID
import cz.frantisekmasa.wfrp_master.compendium.domain.Miracle.Companion as CompendiumMiracle

@Composable
internal fun NonCompendiumMiracleForm(
    viewModel: MiraclesViewModel,
    existingMiracle: Miracle?,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumMiracleFormData.fromMiracle(existingMiracle)

    FormDialog(
        title = if (existingMiracle != null) R.string.title_miracle_new else R.string.title_miracle_edit,
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = viewModel::saveItem,
    ) { validate ->
        TextInput(
            label = stringResource(R.string.label_name),
            value = formData.name,
            validate = validate,
            maxLength = CompendiumMiracle.NAME_MAX_LENGTH
        )

        TextInput(
            label = stringResource(R.string.label_miracle_cult_name),
            value = formData.cultName,
            validate = validate,
            maxLength = CompendiumMiracle.CULT_NAME_MAX_LENGTH
        )

        TextInput(
            label = stringResource(R.string.label_range),
            value = formData.range,
            validate = validate,
            maxLength = CompendiumMiracle.RANGE_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_target),
            value = formData.target,
            validate = validate,
            maxLength = CompendiumMiracle.TARGET_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_duration),
            value = formData.duration,
            validate = validate,
            maxLength = CompendiumMiracle.DURATION_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_effect),
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
