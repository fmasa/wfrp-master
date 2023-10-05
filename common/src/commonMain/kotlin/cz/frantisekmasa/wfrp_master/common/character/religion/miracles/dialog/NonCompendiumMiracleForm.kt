package cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import dev.icerock.moko.resources.compose.stringResource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle.Companion as CompendiumMiracle

@Composable
internal fun NonCompendiumMiracleForm(
    existingMiracle: Miracle?,
    onSave: suspend (Miracle) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumMiracleFormData.fromMiracle(existingMiracle)
    FormDialog(
        title = stringResource(
            if (existingMiracle != null)
                Str.miracles_title_edit
            else Str.miracles_title_new
        ),
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = onSave,
    ) { validate ->
        TextInput(
            label = stringResource(Str.miracles_label_name),
            value = formData.name,
            validate = validate,
            maxLength = CompendiumMiracle.NAME_MAX_LENGTH
        )

        TextInput(
            label = stringResource(Str.miracles_label_cult_name),
            value = formData.cultName,
            validate = validate,
            maxLength = CompendiumMiracle.CULT_NAME_MAX_LENGTH
        )

        TextInput(
            label = stringResource(Str.miracles_label_range),
            value = formData.range,
            validate = validate,
            maxLength = CompendiumMiracle.RANGE_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(Str.miracles_label_target),
            value = formData.target,
            validate = validate,
            maxLength = CompendiumMiracle.TARGET_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(Str.miracles_label_duration),
            value = formData.duration,
            validate = validate,
            maxLength = CompendiumMiracle.DURATION_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(Str.miracles_label_effect),
            value = formData.effect,
            validate = validate,
            maxLength = CompendiumMiracle.EFFECT_MAX_LENGTH,
            multiLine = true,
        )
    }
}

@Stable
private data class NonCompendiumMiracleFormData(
    val id: Uuid,
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
            id = remember(miracle) { miracle?.id ?: uuid4() },
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
