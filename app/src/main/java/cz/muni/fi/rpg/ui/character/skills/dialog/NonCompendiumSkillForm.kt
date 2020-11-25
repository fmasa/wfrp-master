package cz.muni.fi.rpg.ui.character.skills.dialog

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.compendium.common.Characteristic
import cz.muni.fi.rpg.model.domain.skills.Skill
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@Composable
internal fun NonCompendiumSkillForm(
    viewModel: SkillsViewModel,
    existingSkill: Skill?,
    onComplete: () -> Unit,
) {
    val formData = NonCompendiumSkillFormData.fromSkill(existingSkill)
    var saving by remember { mutableStateOf(false) }
    var validate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (existingSkill != null)
                                R.string.title_skill_edit else
                                R.string.title_skill_new
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
                                viewModel.saveSkill(formData.toSkill())
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(FormInputHorizontalPadding),
                verticalAlignment = Alignment.Bottom,
            ) {
                TextInput(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.label_skill_name),
                    value = formData.name.value,
                    onValueChange = { formData.name.value = it },
                    validate = validate,
                    maxLength = Skill.NAME_MAX_LENGTH,
                    rules = Rules(Rules.NotBlank())
                )

                NumberPicker(
                    label = stringResource(R.string.label_advances),
                    value = formData.advances.value,
                    onIncrement = { formData.advances.value++ },
                    onDecrement = {
                        if (formData.advances.value > 1) {
                            formData.advances.value--
                        }
                    }
                )
            }

            TextInput(
                label = stringResource(R.string.label_skill_description),
                value = formData.description.value,
                onValueChange = { formData.description.value = it },
                validate = validate,
                multiLine = true,
                maxLength = Skill.DESCRIPTION_MAX_LENGTH,
            )

            ChipList(
                label = stringResource(R.string.label_skill_characteristic),
                items = Characteristic.values().map { it to it.getShortcutName() },
                value = formData.characteristic.value,
                onValueChange = { formData.characteristic.value = it }
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.TopCenter
            ) {
                CheckboxWithText(
                    text = stringResource(R.string.label_skill_advanced),
                    checked = formData.advanced.value,
                    onCheckedChange = { formData.advanced.value = it },
                )
            }
        }
    }
}

private class NonCompendiumSkillFormData(
    val id: UUID,
    val name: MutableState<String>,
    val description: MutableState<String>,
    val characteristic: MutableState<Characteristic>,
    val advanced: MutableState<Boolean>,
    val advances: MutableState<Int>,
) : FormData {
    companion object {
        @Composable
        fun fromSkill(skill: Skill?): NonCompendiumSkillFormData = NonCompendiumSkillFormData(
            id = remember { skill?.id ?: UUID.randomUUID() },
            name = savedInstanceState { skill?.name ?: "" },
            description = savedInstanceState { skill?.description ?: "" },
            characteristic = savedInstanceState {
                skill?.characteristic ?: Characteristic.values().first()
            },
            advanced = savedInstanceState { skill?.advanced ?: false },
            advances = savedInstanceState { skill?.advances ?: 1 }
        )
    }

    fun toSkill(): Skill = Skill(
        id = id,
        compendiumId = null,
        advanced = advanced.value,
        characteristic = characteristic.value,
        name = name.value,
        description = description.value,
        advances = advances.value
    )

    override fun isValid() =
        name.value.isNotBlank() && name.value.length <= Skill.NAME_MAX_LENGTH &&
            description.value.length <= Skill.DESCRIPTION_MAX_LENGTH &&
            advances.value >= 0
}