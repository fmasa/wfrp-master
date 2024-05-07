package cz.frantisekmasa.wfrp_master.common.partyList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.AlertDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LeavePartyDialog(
    party: Party,
    screenModel: PartyListScreenModel,
    onDismissRequest: () -> Unit,
) {
    var removing by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                Text(stringResource(Str.parties_messages_leave_confirmation))
            }
        },
        dismissButton = {
            TextButton(
                enabled = !removing,
                onClick = onDismissRequest,
            ) {
                Text(stringResource(Str.common_ui_button_cancel).uppercase())
            }
        },
        confirmButton = {
            val coroutineScope = rememberCoroutineScope()
            val userId = LocalUser.current.id

            TextButton(
                enabled = !removing,
                onClick = {
                    removing = true
                    coroutineScope.launch(Dispatchers.IO) {
                        screenModel.leaveParty(party.id, userId)

                        onDismissRequest()
                    }
                },
            ) {
                Text(stringResource(Str.parties_button_leave).uppercase())
            }
        },
    )
}
