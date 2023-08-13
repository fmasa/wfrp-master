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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDialog
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemFormData
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SpellDialog(
    spell: Spell?,
    onDismissRequest: () -> Unit,
    onSaveRequest: suspend (Spell) -> Unit,
) {
    val formData = SpellFormData.fromItem(spell)

    CompendiumItemDialog(
        title = stringResource(
            if (spell == null)
                Str.spells_title_add
            else Str.spells_title_edit
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
                label = stringResource(Str.spells_label_name),
                value = formData.name,
                validate = validate,
                maxLength = Spell.NAME_MAX_LENGTH
            )

            val lores = SpellLore.values().map { it to it.localizedName }
            SelectBox(
                label = stringResource(Str.spells_label_lore),
                value = formData.lore.value,
                onValueChange = { formData.lore.value = it },
                items = remember(lores) { lores.sortedBy { it.second } } +
                    (null to stringResource(Str.spells_lores_other)),
            )

            TextInput(
                label = stringResource(Str.spells_label_lore_legacy),
                value = formData.customLore,
                validate = validate,
                maxLength = Spell.LORE_MAX_LENGTH
            )

            TextInput(
                label = stringResource(Str.spells_label_range),
                value = formData.range,
                validate = validate,
                maxLength = Spell.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.spells_label_target),
                value = formData.target,
                validate = validate,
                maxLength = Spell.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.spells_label_duration),
                value = formData.duration,
                validate = validate,
                maxLength = Spell.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(Str.spells_label_casting_number),
                value = formData.castingNumber,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                validate = validate,
                maxLength = 2,
            )

            TextInput(
                label = stringResource(Str.spells_label_effect),
                value = formData.effect,
                validate = validate,
                maxLength = Spell.EFFECT_MAX_LENGTH,
                multiLine = true,
                helperText = stringResource(Str.common_ui_markdown_supported_note),
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
