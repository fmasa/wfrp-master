package cz.frantisekmasa.wfrp_master.common.partyList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.connectivity.CouldNotConnectToBackend
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.InfoIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.settings.Language
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreatePartyDialog(
    screenModel: PartyListScreenModel,
    onSuccess: (PartyId) -> Unit,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        var validate by remember { mutableStateOf(false) }
        val partyName = inputValue("", Rules.NotBlank())
        var language by rememberSaveable { mutableStateOf(Language.EN) }

        Scaffold(
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                val userId = LocalUser.current.id

                var saving by remember { mutableStateOf(false) }

                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(Str.parties_title_create_party)) },
                    actions = {
                        val snackbarHolder = LocalPersistentSnackbarHolder.current

                        val errorUnknown = stringResource(Str.messages_error_unknown)
                        val errorNoConnection = stringResource(Str.parties_messages_create_error_no_connection)
                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                if (!partyName.isValid()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        val partyId = screenModel.createParty(
                                            partyName.value,
                                            language,
                                            userId,
                                        )

                                        onSuccess(partyId)
                                    } catch (e: CouldNotConnectToBackend) {
                                        Napier.i("User could not assemble party, because (s)he is offline", e)

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
                    value = partyName,
                    validate = validate,
                    maxLength = Party.NAME_MAX_LENGTH,
                )

                val label = stringResource(Str.parties_label_language)

                InputLabel(label)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.weight(1f)) {
                        SelectBox(
                            value = language,
                            onValueChange = { language = it },
                            items = remember {
                                Language.values()
                                    .map { it to it.localizedName }
                                    .sortedBy { it.second }
                            },
                        )
                    }

                    InfoIcon(
                        title = label,
                        text = stringResource(Str.parties_language_explanation)
                    )
                }
            }
        }
    }
}
