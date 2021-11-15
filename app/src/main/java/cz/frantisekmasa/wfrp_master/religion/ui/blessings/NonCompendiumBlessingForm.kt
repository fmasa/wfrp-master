package cz.frantisekmasa.wfrp_master.religion.ui.blessings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.common.core.ui.components.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.religion.domain.Blessing
import cz.muni.fi.rpg.R
import java.util.UUID

@Composable
internal fun NonCompendiumBlessingForm(
    viewModel: BlessingsViewModel,
    existingBlessing: Blessing?,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumBlessingFormData.fromBlessing(existingBlessing)

    FormDialog(
        title = if (existingBlessing != null) R.string.title_blessing_new else R.string.title_blessing_edit,
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = viewModel::saveItem,
    ) { validate ->
        TextInput(
            label = stringResource(R.string.label_name),
            value = formData.name,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.NAME_MAX_LENGTH
        )

        TextInput(
            label = stringResource(R.string.label_range),
            value = formData.range,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.RANGE_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_target),
            value = formData.target,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.TARGET_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_duration),
            value = formData.duration,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.DURATION_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_effect),
            value = formData.effect,
            validate = validate,
            maxLength = cz.frantisekmasa.wfrp_master.compendium.domain.Blessing.EFFECT_MAX_LENGTH,
            multiLine = true,
        )
    }
}

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
