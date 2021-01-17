package cz.muni.fi.rpg.ui.partyList

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
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
        var partyName by savedInstanceState { "" }

        Scaffold(
            topBar = {
                val coroutineScope = rememberCoroutineScope()
                val context = AmbientContext.current
                val userId = AmbientUser.current.id

                var saving by remember { mutableStateOf(false) }

                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(R.string.assembleParty_title)) },
                    actions = {
                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                if (partyName.isBlank()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    try {
                                        val partyId = viewModel.createParty(partyName, userId)

                                        withContext(Dispatchers.Main) { onSuccess(partyId) }
                                    } catch (e: CouldNotConnectToBackend) {
                                        Timber.i(e,"User could not assemble party, because (s)he is offline")
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
            ScrollableColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
                TextInput(
                    label = stringResource(R.string.label_party_name),
                    value = partyName,
                    onValueChange = { partyName = it },
                    validate = validate,
                    rules = Rules(Rules.NotBlank()),
                    maxLength = Party.NAME_MAX_LENGTH,
                )
            }
        }
    }
}
