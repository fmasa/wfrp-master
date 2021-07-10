package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.longToast
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.CouldNotConnectToBackend
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun CreatePartyDialog(
    viewModel: PartyListViewModel,
    onSuccess: (PartyId) -> Unit,
    onDismissRequest: () -> Unit,
) {

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        var validate by remember { mutableStateOf(false) }
        val partyName = inputValue("", Rules.NotBlank())

        Scaffold(
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                val context = LocalContext.current
                val userId = LocalUser.current.id

                var saving by remember { mutableStateOf(false) }

                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(R.string.assembleParty_title)) },
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
                                        Timber.i(e, "User could not assemble party, because (s)he is offline")
                                        longToast(context, R.string.error_party_creation_no_connection)
                                    } catch (e: Throwable) {
                                        longToast(context, R.string.error_unkown)
                                        Timber.e(e)
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
                    label = stringResource(R.string.label_party_name),
                    value = partyName,
                    validate = validate,
                    maxLength = Party.NAME_MAX_LENGTH,
                )
            }
        }
    }
}
