package cz.frantisekmasa.wfrp_master.common.compendium.spell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun SpellDialog(
    spell: Spell?,
    onDismissRequest: () -> Unit,
    onSaveRequest: suspend (Spell) -> Unit,
) {
    val formData = SpellFormData.fromItem(spell)

    val strings = LocalStrings.current.spells

    CompendiumItemDialog(
        title = if (spell == null) strings.titleAdd else strings.titleEdit,
        formData = formData,
        saver = onSaveRequest,
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
                maxLength = Spell.NAME_MAX_LENGTH
            )

            TextInput(
                label = strings.labelLore,
                value = formData.lore,
                validate = validate,
                maxLength = Spell.LORE_MAX_LENGTH
            )

            TextInput(
                label = strings.labelRange,
                value = formData.range,
                validate = validate,
                maxLength = Spell.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = strings.labelTarget,
                value = formData.target,
                validate = validate,
                maxLength = Spell.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = strings.labelDuration,
                value = formData.duration,
                validate = validate,
                maxLength = Spell.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = strings.labelCastingNumber,
                value = formData.castingNumber,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                validate = validate,
                maxLength = 2,
            )

            TextInput(
                label = strings.labelEffect,
                value = formData.effect,
                validate = validate,
                maxLength = Spell.EFFECT_MAX_LENGTH,
                multiLine = true,
                helperText = LocalStrings.current.commonUi.markdownSupportedNote,
            )
        }
    }
}

@Stable
private data class SpellFormData(
    val id: Uuid,
    val name: InputValue,
    val lore: InputValue,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val castingNumber: InputValue,
    val effect: InputValue,
    val isVisibleToPlayers: Boolean,
) : CompendiumItemFormData<Spell> {
    companion object {
        @Composable
        fun fromItem(item: Spell?) = SpellFormData(
            id = remember(item) { item?.id ?: uuid4() },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            lore = inputValue(item?.lore ?: ""),
            range = inputValue(item?.range ?: ""),
            target = inputValue(item?.target ?: ""),
            duration = inputValue(item?.duration ?: ""),
            castingNumber = inputValue(
                item?.castingNumber?.toString() ?: "0",
                Rules.NonNegativeInteger(),
            ),
            effect = inputValue(item?.effect ?: ""),
            isVisibleToPlayers = item?.isVisibleToPlayers ?: false,
        )
    }

    override fun toValue() = Spell(
        id = id,
        name = name.value,
        lore = lore.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        castingNumber = castingNumber.value.toInt(),
        effect = effect.value,
        isVisibleToPlayers = isVisibleToPlayers,
    )

    override fun isValid() =
        listOf(name, lore, range, target, duration, castingNumber, effect).all { it.isValid() }
}
