package cz.muni.fi.rpg.ui.compendium

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraintsScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.compendium.Talent
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.composables.dialog.DialogState
import cz.muni.fi.rpg.ui.common.composables.dialog.CancelButton
import cz.muni.fi.rpg.ui.common.composables.dialog.Progress
import cz.muni.fi.rpg.ui.common.composables.dialog.SaveButton
import cz.muni.fi.rpg.viewModels.CompendiumViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@ExperimentalLayout
@Composable
fun WithConstraintsScope.TalentCompendiumTab(viewModel: CompendiumViewModel) {
    val coroutineScope = rememberCoroutineScope()

    CompendiumTab(
        liveItems = viewModel.talents,
        emptyUI = {
            EmptyUI(
                textId = R.string.no_talents_in_compendium,
                subTextId = R.string.no_talents_in_compendium_sub_text,
                drawableResourceId = R.drawable.ic_skills
            )
        },
        onRemove = { coroutineScope.launch(Dispatchers.IO) { viewModel.remove(it) } },
        dialog = { TalentDialog(it, viewModel) },
        width = maxWidth,
    ) { talent ->
        ListItem(
            icon = { ItemIcon(R.drawable.ic_skills) },
            text = { Text(talent.name) }
        )
        Divider()
    }
}

private data class TalentFormData(
    val id: UUID,
    val name: MutableState<String>,
    val description: MutableState<String>,
) : FormData {
    companion object {
        @Composable
        fun fromState(state: DialogState.Opened<Talent?>) = TalentFormData(
            id = remember(state) { state.item?.id ?: UUID.randomUUID() },
            name = savedInstanceState(state) { state.item?.name ?: "" },
            description = savedInstanceState(state) { state.item?.description ?: "" },
        )
    }

    fun toTalent() = Talent(
        id = id,
        name = name.value,
        description = description.value,
    )

    override fun isValid() =
        name.value.isNotBlank() &&
                name.value.length <= Talent.NAME_MAX_LENGTH &&
                description.value.length <= Talent.DESCRIPTION_MAX_LENGTH
}

private enum class TalentFormState {
    EDITED_BY_USER,
    SAVING_TALENT,
}

@ExperimentalLayout
@Composable
private fun TalentDialog(
    dialogState: MutableState<DialogState<Talent?>>,
    viewModel: CompendiumViewModel
) {
    val dialogStateValue = dialogState.value

    if (dialogStateValue !is DialogState.Opened) {
        return
    }

    val formData = TalentFormData.fromState(dialogStateValue)
    val validate = remember { mutableStateOf(false) }
    val formState = remember { mutableStateOf(TalentFormState.EDITED_BY_USER) }

    AlertDialog(
        onDismissRequest = { dialogState.value = DialogState.Closed() },
        text = {
            if (formState.value == TalentFormState.SAVING_TALENT) {
                Progress()
                return@AlertDialog
            }

            ScrollableColumn {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextInput(
                        label = stringResource(R.string.label_name),
                        value = formData.name.value,
                        onValueChange = { formData.name.value = it },
                        validate = validate.value,
                        maxLength = Talent.NAME_MAX_LENGTH
                    )

                    TextInput(
                        label = stringResource(R.string.label_description),
                        value = formData.description.value,
                        onValueChange = { formData.description.value = it },
                        validate = validate.value,
                        maxLength = Talent.DESCRIPTION_MAX_LENGTH,
                        multiLine = true,
                    )
                }
            }
        },
        buttons = {
            Box(Modifier.fillMaxWidth().padding(bottom = 8.dp, end = 8.dp)) {
                FlowRow(
                    mainAxisSize = SizeMode.Expand,
                    mainAxisAlignment = MainAxisAlignment.End,
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 12.dp
                ) {
                    CancelButton(onClick = { dialogState.value = DialogState.Closed() })

                    val coroutineScope = rememberCoroutineScope()

                    SaveButton(onClick = {
                        if (formData.isValid()) {
                            formState.value = TalentFormState.SAVING_TALENT
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.save(formData.toTalent())
                                withContext(Dispatchers.Main) {
                                    dialogState.value = DialogState.Closed()
                                }
                            }
                        } else {
                            validate.value = true
                        }
                    })
                }
            }
        }
    )
}
