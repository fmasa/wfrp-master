package cz.muni.fi.rpg.ui.character.spells.dialog

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
                value = formData.name.value,
                onValueChange = { formData.name.value = it },
                validate = validate,
                maxLength = cz.muni.fi.rpg.model.domain.compendium.Spell.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_spell_range),
                value = formData.range.value,
                onValueChange = { formData.range.value = it },
                validate = validate,
                maxLength = cz.muni.fi.rpg.model.domain.compendium.Spell.RANGE_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_target),
                value = formData.target.value,
                onValueChange = { formData.target.value = it },
                validate = validate,
                maxLength = cz.muni.fi.rpg.model.domain.compendium.Spell.TARGET_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_duration),
                value = formData.duration.value,
                onValueChange = { formData.duration.value = it },
                validate = validate,
                maxLength = cz.muni.fi.rpg.model.domain.compendium.Spell.DURATION_MAX_LENGTH,
            )

            TextInput(
                label = stringResource(R.string.label_spell_casting_number),
                value = formData.castingNumber.value,
                keyboardType = KeyboardType.Number,
                onValueChange = { formData.castingNumber.value = it },
                validate = validate,
                maxLength = 2,
            )

            TextInput(
                label = stringResource(R.string.label_spell_effect),
                value = formData.effect.value,
                onValueChange = { formData.effect.value = it },
                validate = validate,
                maxLength = cz.muni.fi.rpg.model.domain.compendium.Spell.EFFECT_MAX_LENGTH,
                multiLine = true,
            )
        }
    }
}

private class NonCompendiumSpellFormData(
    val id: UUID,
    val name: MutableState<String>,
    val range: MutableState<String>,
    val target: MutableState<String>,
    val duration: MutableState<String>,
    val castingNumber: MutableState<String>,
    val effect: MutableState<String>,
) : FormData {
    companion object {
        @Composable
        fun fromSpell(item: Spell?): NonCompendiumSpellFormData = NonCompendiumSpellFormData(
            id = item?.id ?: UUID.randomUUID(),
            name = savedInstanceState { item?.name ?: "" },
            range = savedInstanceState { item ?. range ?: "" },
            target = savedInstanceState { item?.target ?: "" },
            duration = savedInstanceState{ item?.duration ?: "" },
            castingNumber = savedInstanceState { item?.castingNumber?.toString() ?: "" },
            effect = savedInstanceState { item?.effect ?: "" },
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
        name.value.isNotBlank() &&
                name.value.length <= Spell.NAME_MAX_LENGTH &&
                range.value.length <= Spell.RANGE_MAX_LENGTH &&
                target.value.length <= Spell.TARGET_MAX_LENGTH &&
                duration.value.length <= Spell.DURATION_MAX_LENGTH &&
                (castingNumber.value.toIntOrNull() ?: 0) >= 0 &&
                effect.value.length <= Spell.EFFECT_MAX_LENGTH
}