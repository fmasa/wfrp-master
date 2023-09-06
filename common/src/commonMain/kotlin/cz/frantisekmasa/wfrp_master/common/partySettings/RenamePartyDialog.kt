package cz.frantisekmasa.wfrp_master.common.partySettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RenamePartyDialog(
    currentName: String,
    viewModel: PartySettingsScreenModel,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        var validate by remember { mutableStateOf(false) }
        val newName = inputValue(currentName, Rules.NotBlank())

        Scaffold(
            topBar = {
                val coroutineScope = rememberCoroutineScope()

                var saving by remember { mutableStateOf(false) }

                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(Str.parties_title_rename)) },
                    actions = {
                        val snackbarHolder = LocalPersistentSnackbarHolder.current

                        val partyUpdatedMessage = stringResource(Str.parties_messages_party_updated)
                        val errorUnknown = stringResource(Str.messages_error_unknown)
                        val errorNoConnection = stringResource(Str.parties_messages_update_error_no_connection)
                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                if (!newName.isValid()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        viewModel.renameParty(newName.value)
                                        snackbarHolder.showSnackbar(
                                            partyUpdatedMessage,
                                            duration = SnackbarDuration.Short,
                                        )

                                        Napier.d("Party was renamed")

                                        onDismissRequest()

                                        return@launch
                                    } catch (e: CouldNotConnectToBackend) {
                                        Napier.i(
                                            "User could not rename party, because (s)he is offline",
                                            e,
                                        )
                                        snackbarHolder.showSnackbar(
                                            errorNoConnection,
                                            duration = SnackbarDuration.Long,
                                        )
                                    } catch (e: Throwable) {
                                        snackbarHolder.showSnackbar(
                                            errorUnknown,
                                            duration = SnackbarDuration.Long,
                                        )
                                        Napier.e(e.toString(), e)
                                    }

                                    saving = false
                                }
                            }
                        )
                    }
                )
            }
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
            ) {
                TextInput(
                    label = stringResource(Str.parties_label_name),
                    value = newName,
                    validate = validate,
                    maxLength = Party.NAME_MAX_LENGTH,
                )
            }
        }
    }
}
