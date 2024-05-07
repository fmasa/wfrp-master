package cz.frantisekmasa.wfrp_master.common.compendium.blessing

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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun BlessingDialog(
    blessing: Blessing?,
    onDismissRequest: () -> Unit,
    onSaveRequest: suspend (Blessing) -> Unit,
) {
    val formData = BlessingFormData.fromItem(blessing)

    CompendiumItemDialog(
        title =
            stringResource(
                if (blessing == null) {
                    Str.blessings_title_new
                } else {
                    Str.blessings_title_edit
                },
            ),
        formData = formData,
        saver = onSaveRequest,
        onDismissRequest = onDismissRequest,
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = stringResource(Str.blessings_label_name),
                value = formData.name,
                validate = validate,
                maxLength = Blessing.NAME_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.blessings_label_range),
                value = formData.range,
                validate = validate,
                maxLength = Blessing.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.blessings_label_target),
                value = formData.target,
                validate = validate,
                maxLength = Blessing.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.blessings_label_duration),
                value = formData.duration,
                validate = validate,
                maxLength = Blessing.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.blessings_label_effect),
                value = formData.effect,
                validate = validate,
                maxLength = Blessing.EFFECT_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
            )
        }
    }
}

@Stable
private data class BlessingFormData(
    val id: Uuid,
    val name: InputValue,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val effect: InputValue,
    val isVisibleToPlayers: Boolean,
) : CompendiumItemFormData<Blessing> {
    companion object {
        @Composable
        fun fromItem(item: Blessing?) =
            BlessingFormData(
                id = remember(item) { item?.id ?: uuid4() },
                name = inputValue(item?.name ?: "", Rules.NotBlank()),
                range = inputValue(item?.range ?: ""),
                target = inputValue(item?.target ?: ""),
                duration = inputValue(item?.duration ?: ""),
                effect = inputValue(item?.effect ?: ""),
                isVisibleToPlayers = item?.isVisibleToPlayers ?: false,
            )
    }

    override fun toValue() =
        Blessing(
            id = id,
            name = name.value,
            range = range.value,
            target = target.value,
            duration = duration.value,
            effect = effect.value,
            isVisibleToPlayers = isVisibleToPlayers,
        )

    override fun isValid() = listOf(name, range, target, duration, effect).all { it.isValid() }
}
