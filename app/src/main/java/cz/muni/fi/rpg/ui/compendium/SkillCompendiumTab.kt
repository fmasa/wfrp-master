package cz.muni.fi.rpg.ui.compendium

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraintsScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.compendium.CompendiumItem
import cz.muni.fi.rpg.model.domain.compendium.Skill
import cz.muni.fi.rpg.model.domain.compendium.common.Characteristic
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.composables.dialog.*
import cz.muni.fi.rpg.viewModels.CompendiumViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalLayout
@Composable
fun WithConstraintsScope.SkillCompendiumTab(viewModel: CompendiumViewModel) {
    val coroutineScope = rememberCoroutineScope()

    CompendiumTab(
        liveItems = viewModel.skills,
        emptyUI = {
            EmptyUI(
                textId = R.string.no_skills_in_compendium,
                subTextId = R.string.no_skills_in_compendium_sub_text,
                drawableResourceId = R.drawable.ic_skills
            )
        },
        onRemove = { coroutineScope.launch(Dispatchers.IO) { viewModel.remove(it) } },
        dialog = { SkillDialog(it, viewModel) },
        width = maxWidth,
    ) { skill ->
        ListItem(
            icon = { ItemIcon(skill.characteristic.getIconId()) },
            text = { Text(skill.name) }
        )
        Divider()
    }
}

interface CompendiumItemFormData<T : CompendiumItem> : FormData {
    fun toItem(): T
}

private data class SkillFormData(
    val id: UUID,
    val name: MutableState<String>,
    val description: MutableState<String>,
    val characteristic: MutableState<Characteristic>,
    val advanced: MutableState<Boolean>,
) : CompendiumItemFormData<Skill> {
    companion object {
        @Composable
        fun fromState(state: DialogState.Opened<Skill?>) = SkillFormData(
            id = remember(state) { state.item?.id ?: UUID.randomUUID() },
            name = savedInstanceState(state) { state.item?.name ?: "" },
            description = savedInstanceState(state) { state.item?.description ?: "" },
            characteristic = savedInstanceState(state) {
                state.item?.characteristic ?: Characteristic.AGILITY
            },
            advanced = savedInstanceState(state) { state.item?.advanced ?: false },
        )
    }

    override fun toItem() = Skill(
        id = id,
        name = name.value,
        description = description.value,
        characteristic = characteristic.value,
        advanced = advanced.value
    )

    override fun isValid() =
        name.value.isNotBlank() &&
                name.value.length <= Skill.NAME_MAX_LENGTH &&
                description.value.length <= Skill.DESCRIPTION_MAX_LENGTH
}

@ExperimentalLayout
@Composable
private fun SkillDialog(
    dialogState: MutableState<DialogState<Skill?>>,
    viewModel: CompendiumViewModel,
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val formData = SkillFormData.fromState(dialogStateValue)

    CompendiumItemDialog(
        title = stringResource(
            if (dialogStateValue.item == null)
                R.string.title_addSkill
            else R.string.title_skill_edit
        ),
        formData = formData,
        saver = viewModel::save,
        onDismissRequest = {dialogState.value = DialogState.Closed()}
    ) { validate ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            TextInput(
                label = stringResource(R.string.label_name),
                value = formData.name.value,
                onValueChange = { formData.name.value = it },
                validate = validate,
                maxLength = Skill.NAME_MAX_LENGTH
            )

            TextInput(
                label = stringResource(R.string.label_description),
                value = formData.description.value,
                onValueChange = { formData.description.value = it },
                validate = validate,
                maxLength = Skill.DESCRIPTION_MAX_LENGTH,
                multiLine = true,
            )

            ChipList(
                label = stringResource(R.string.label_skill_characteristic),
                items = Characteristic.values()
                    .map { it to stringResource(it.getShortcutNameId()) },
                value = formData.characteristic.value,
                onValueChange = { formData.characteristic.value = it }
            )

            Box(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                alignment = Alignment.TopCenter,
            ) {
                CheckboxWithText(
                    text = stringResource(R.string.label_skill_advanced),
                    checked = formData.advanced.value,
                    onCheckedChange = { formData.advanced.value = it }
                )
            }
        }
    }
}
