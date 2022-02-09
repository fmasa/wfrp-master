package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RemovePartyDialog(
    party: Party,
    viewModel: PartyListViewModel,
    onDismissRequest: () -> Unit
) {
    var removing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            val messages = LocalStrings.current.parties.messages

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                Text(messages.removalConfirmation)

                if (party.getPlayerCounts() > 0) {
                    Text(
                        messages.membersWillLoseAccess,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                enabled = !removing,
                onClick = onDismissRequest
            ) {
                Text(LocalStrings.current.commonUi.buttonCancel.uppercase())
            }
        },
        confirmButton = {
            val coroutineScope = rememberCoroutineScope()
            val messages = LocalStrings.current.messages
            val snackbarHolder = LocalPersistentSnackbarHolder.current

            TextButton(
                enabled = !removing,
                onClick = {
                    removing = true
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.archive(party.id)

                        withContext(Dispatchers.Main) {
                            onDismissRequest()
                            snackbarHolder.showSnackbar(
                                messages.partyRemoved,
                                duration = SnackbarDuration.Long,
                            )
                        }
                    }
                },
            ) {
                Text(LocalStrings.current.commonUi.buttonRemove.uppercase())
            }
        }
    )
}
