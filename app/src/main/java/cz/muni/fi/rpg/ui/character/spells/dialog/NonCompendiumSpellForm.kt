package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.core.ui.components.FormDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import java.util.UUID

@Composable
internal fun NonCompendiumSpellForm(
    viewModel: SpellsViewModel,
    existingSpell: Spell?,
    onDismissRequest: () -> Unit,
) {
    val formData = NonCompendiumSpellFormData.fromSpell(existingSpell)

    FormDialog(
        title = if (existingSpell != null) R.string.title_spell_edit else R.string.title_spell_new,
        onDismissRequest = onDismissRequest,
        formData = formData,
        onSave = viewModel::saveSpell,
    ) { validate ->
        TextInput(
            label = stringResource(R.string.label_name),
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
                stringResource(R.string.spell_memorized),
                checked = formData.memorized.value,
                onCheckedChange = { formData.memorized.value = it },
            )
        }

        TextInput(
            label = stringResource(R.string.label_range),
            value = formData.range,
            validate = validate,
            maxLength = Spell.RANGE_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_target),
            value = formData.target,
            validate = validate,
            maxLength = Spell.TARGET_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_duration),
            value = formData.duration,
            validate = validate,
            maxLength = Spell.DURATION_MAX_LENGTH,
        )

        TextInput(
            label = stringResource(R.string.label_spell_casting_number),
            value = formData.castingNumber,
            keyboardType = KeyboardType.Number,
            validate = validate,
            maxLength = 2,
        )

        TextInput(
            label = stringResource(R.string.label_effect),
            value = formData.effect,
            validate = validate,
            maxLength = Spell.EFFECT_MAX_LENGTH,
            multiLine = true,
        )
    }
}

private class NonCompendiumSpellFormData(
    val id: UUID,
    val name: InputValue,
    val memorized: MutableState<Boolean>,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val castingNumber: InputValue,
    val effect: InputValue,
) : HydratedFormData<Spell> {
    companion object {
        @Composable
        fun fromSpell(item: Spell?): NonCompendiumSpellFormData = NonCompendiumSpellFormData(
            id = item?.id ?: UUID.randomUUID(),
            memorized = rememberSaveable { mutableStateOf(item?.memorized ?: false) },
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            range = inputValue(item?.range ?: ""),
            target = inputValue(item?.target ?: ""),
            duration = inputValue(item?.duration ?: ""),
            castingNumber = inputValue(
                item?.castingNumber?.toString() ?: "",
                Rules.PositiveInteger(),
            ),
            effect = inputValue(item?.effect ?: ""),
        )
    }

    override fun toValue(): Spell = Spell(
        id = id,
        name = name.value,
        memorized = memorized.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        castingNumber = castingNumber.value.toInt(),
        effect = effect.value,
    )

    override fun isValid() =
        listOf(name, range, target, duration, castingNumber, effect).all { it.isValid() }
}
