package cz.frantisekmasa.wfrp_master.common.compendium.spell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
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

    val strings = LocalStrings.current

    CompendiumItemDialog(
        title = if (spell == null) strings.spells.titleAdd else strings.spells.titleEdit,
        formData = formData,
        saver = onSaveRequest,
        onDismissRequest = onDismissRequest,
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(Spacing.bodyPadding),
        ) {
            TextInput(
                label = strings.spells.labelName,
                value = formData.name,
                validate = validate,
                maxLength = Spell.NAME_MAX_LENGTH
            )

            SelectBox(
                label = strings.spells.labelLore,
                value = formData.lore.value,
                onValueChange = { formData.lore.value = it },
                items = remember(strings) {
                    SpellLore.values().map { it to it.nameResolver(strings) }
                        .sortedBy { it.second } + (null to strings.spells.lores.other)
                },
            )

            TextInput(
                label = strings.spells.labelLoreLegacy,
                value = formData.customLore,
                validate = validate,
                maxLength = Spell.LORE_MAX_LENGTH
            )

            TextInput(
                label = strings.spells.labelRange,
                value = formData.range,
                validate = validate,
                maxLength = Spell.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = strings.spells.labelTarget,
                value = formData.target,
                validate = validate,
                maxLength = Spell.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = strings.spells.labelDuration,
                value = formData.duration,
                validate = validate,
                maxLength = Spell.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = strings.spells.labelCastingNumber,
                value = formData.castingNumber,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                validate = validate,
                maxLength = 2,
            )

            TextInput(
                label = strings.spells.labelEffect,
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
    val isNew: Boolean,
    val name: InputValue,
    val customLore: InputValue,
    val lore: MutableState<SpellLore?>,
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
            isNew = item == null,
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            customLore = inputValue(item?.customLore ?: ""),
            lore = rememberSaveable(item) { mutableStateOf(item?.lore) },
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
        customLore = customLore.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        castingNumber = castingNumber.value.toInt(),
        effect = effect.value,
        isVisibleToPlayers = isVisibleToPlayers,
    )

    override fun isValid() =
        listOf(name, customLore, range, target, duration, castingNumber, effect).all { it.isValid() } &&
            (lore.value != null || !isNew)
}
