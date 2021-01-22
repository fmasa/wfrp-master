package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.core.ui.forms.*
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.SpellsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
internal fun NonCompendiumSpellForm(
    viewModel: SpellsViewModel,
    existingSpell: Spell?,
    onComplete: () -> Unit,
) {
    val formData = NonCompendiumSpellFormData.fromSpell(existingSpell)
    var saving by remember { mutableStateOf(false) }
    var validate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (existingSpell != null)
                                R.string.title_spell_edit else
                                R.string.title_spell_new
                        )
                    )
                },
                actions = {
                    val coroutineScope = rememberCoroutineScope()

                    SaveAction(
                        enabled = !saving,
                        onClick = {
                            if (!formData.isValid()) {
                                validate = true
                                return@SaveAction
                            }

                            coroutineScope.launch(Dispatchers.IO) {
                                saving = true
                                viewModel.saveSpell(formData.toSpell())
                                withContext(Dispatchers.Main) { onComplete() }
                            }
                        }
                    )
                }
            )
        }
    ) {
        if (saving) {
            FullScreenProgress()
            return@Scaffold
        }

        ScrollableColumn(
            contentPadding = PaddingValues(BodyPadding),
            verticalArrangement = Arrangement.spacedBy(FormInputVerticalPadding)
        ) {
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name,
                validate = validate,
                maxLength = Spell.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_spell_range),
                value = formData.range,
                validate = validate,
                maxLength = Spell.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_target),
                value = formData.target,
                validate = validate,
                maxLength = Spell.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_duration),
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
                label = stringResource(R.string.label_spell_effect),
                value = formData.effect,
                validate = validate,
                maxLength = Spell.EFFECT_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}

private class NonCompendiumSpellFormData(
    val id: UUID,
    val name: InputValue,
    val range: InputValue,
    val target: InputValue,
    val duration: InputValue,
    val castingNumber: InputValue,
    val effect: InputValue,
) : FormData {
    companion object {
        @Composable
        fun fromSpell(item: Spell?): NonCompendiumSpellFormData = NonCompendiumSpellFormData(
            id = item?.id ?: UUID.randomUUID(),
            name = inputValue(item?.name ?: "", Rules.NotBlank()),
            range = inputValue(item ?. range ?: ""),
            target = inputValue(item?.target ?: ""),
            duration = inputValue(item?.duration ?: ""),
            castingNumber = inputValue(
                item?.castingNumber?.toString() ?: "",
                Rules.PositiveInteger(),
            ),
            effect = inputValue(item?.effect ?: ""),
        )
    }

    fun toSpell(): Spell = Spell(
        id = id,
        name = name.value,
        range = range.value,
        target = target.value,
        duration = duration.value,
        castingNumber = castingNumber.value.toInt(),
        effect = effect.value,
    )

    override fun isValid() =
        listOf(name, range, target, duration, castingNumber, effect).all { it.isValid() }
}