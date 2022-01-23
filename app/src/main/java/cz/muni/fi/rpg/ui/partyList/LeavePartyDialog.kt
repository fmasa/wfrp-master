package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LeavePartyDialog(party: Party, viewModel: PartyListViewModel, onDismissRequest: () -> Unit) {
    var removing by remember { mutableStateOf(false) }
    val strings = LocalStrings.current.parties

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                Text(strings.messages.leaveConfirmation)
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
            val userId = UserId.fromString(LocalUser.current.id)

            TextButton(
                enabled = !removing,
                onClick = {
                    removing = true
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.leaveParty(party.id, userId)

                        withContext(Dispatchers.Main) { onDismissRequest() }
                    }
                },
            ) {
                Text(strings.buttonLeave.uppercase())
            }
        }
    )
}
