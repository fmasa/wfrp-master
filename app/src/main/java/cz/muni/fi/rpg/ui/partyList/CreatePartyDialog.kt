package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CreatePartyDialog(
    viewModel: PartyListViewModel,
    onSuccess: (PartyId) -> Unit,
    onDismissRequest: () -> Unit,
) {

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        var validate by remember { mutableStateOf(false) }
        val partyName = inputValue("", Rules.NotBlank())
        val strings = LocalStrings.current.parties

        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                val messages = LocalStrings.current.messages
                val userId = LocalUser.current.id

                var saving by remember { mutableStateOf(false) }

                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(strings.titleCreateParty) },
                    actions = {
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
                                        val partyId = viewModel.createParty(partyName.value, userId)

                                        withContext(Dispatchers.Main) { onSuccess(partyId) }
                                    } catch (e: CouldNotConnectToBackend) {
                                        Napier.i("User could not assemble party, because (s)he is offline", e)

                                        scaffoldState.snackbarHostState.showSnackbar(
                                            messages.partyCreateErrorNoConnection,
                                            duration = SnackbarDuration.Long,
                                        )
                                    } catch (e: Throwable) {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            messages.errorUnknown,
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
                    label = strings.labelName,
                    value = partyName,
                    validate = validate,
                    maxLength = Party.NAME_MAX_LENGTH,
                )
            }
        }
    }
}
