package cz.frantisekmasa.wfrp_master.common.character.spells.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun NonCompendiumSpellForm(
    onSave: suspend (Spell) -> Unit,
    existingSpell: Spell?,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumSpellFormData.fromSpell(existingSpell)

    FormDialog(
        title = stringResource(
            if (existingSpell != null)
                Str.spells_title_edit
            else Str.spells_title_new
        ),
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = onSave,
    ) { validate ->
        TextInput(
            label = stringResource(Str.spells_label_name),
            value = formData.name,
            validate = validate,
            maxLength = Spell.NAME_MAX_LENGTH
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.small),
            contentAlignment = Alignment.Center
        ) {
            CheckboxWithText(
                stringResource(Str.spells_label_memorized),
                checked = formData.memorized.value,
                onCheckedChange = { formData.memorized.value = it },
            )
        }

        val lores = SpellLore.values().map { it to it.localizedName }
        SelectBox(
            label = stringResource(Str.spells_label_lore),
            value = formData.lore.value,
            onValueChange = { formData.lore.value = it },
            items = remember(lores) { lores.sortedBy { it.second } } +
                (null to stringResource(Str.spells_lores_other)),
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
        )
    }
}

@Stable
private class NonCompendiumSpellFormData(
    val id: Uuid,
    val name: InputValue,
    val memorized: MutableState<Boolean>,
    val range: InputValue,
    val target: InputValue,
    val lore: MutableState<SpellLore?>,
    val duration: InputValue,
    val castingNumber: InputValue,
    val effect: InputValue,
) : HydratedFormData<Spell> {
    companion object {
        @Composable
        fun fromSpell(item: Spell?): NonCompendiumSpellFormData = NonCompendiumSpellFormData(
            id = item?.id ?: uuid4(),
            memorized = rememberSaveable { mutableStateOf(item?.memorized ?: false) },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            range = inputValue(item?.range ?: ""),
            target = inputValue(item?.target ?: ""),
            duration = inputValue(item?.duration ?: ""),
            castingNumber = inputValue(
                item?.castingNumber?.toString() ?: "",
                Rules.NonNegativeInteger(),
            ),
            effect = inputValue(item?.effect ?: ""),
            lore = rememberSaveable { mutableStateOf(item?.lore) },
        )
    }

    override fun toValue(): Spell = Spell(
        id = id,
        name = name.value,
        memorized = memorized.value,
        range = range.value,
        target = target.value,
        lore = lore.value,
        duration = duration.value,
        castingNumber = castingNumber.value.toInt(),
        effect = effect.value,
    )

    override fun isValid() =
        listOf(name, range, target, duration, castingNumber, effect).all { it.isValid() }
}
