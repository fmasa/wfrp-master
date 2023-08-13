package cz.frantisekmasa.wfrp_master.common.character.notes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardEditButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MotivationCard(character: Character, screenModel: CharacterScreenModel) {
    CardContainer(
        bodyPadding = PaddingValues(start = Spacing.small, end = Spacing.small),
    ) {
        Column {
            var editDialogOpened by remember { mutableStateOf(false) }

            if (editDialogOpened) {
                EditMotivationDialog(
                    character,
                    screenModel,
                    onDismissRequest = { editDialogOpened = false },
                )
            }

            CardTitle(
                stringResource(Str.character_motivation),
                actions = {
                    CardEditButton(onClick = { editDialogOpened = true })
                }
            )

            RichText {
                Markdown(character.motivation)
            }
        }
    }
}

@Composable
private fun EditMotivationDialog(
    character: Character,
    screenModel: CharacterScreenModel,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val motivation = inputValue(character.motivation)

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(Str.character_title_edit_motivation)) },
                    actions = {
                        val coroutineScope = rememberCoroutineScope()
                        var saving by remember { mutableStateOf(false) }

                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    screenModel.update { it.copy(motivation = motivation.value) }

                                    onDismissRequest()
                                }
                            }
                        )
                    }
                )
            },
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                TextInput(
                    label = stringResource(Str.character_motivation),
                    value = motivation,
                    validate = false,
                    maxLength = Character.MOTIVATION_MAX_LENGTH,
                    multiLine = true,
                    helperText = stringResource(Str.common_ui_markdown_supported_note),
                )
            }
        }
    }
}
