package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.frantisekmasa.wfrp_master.core.auth.UserId
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LeavePartyDialog(party: Party, viewModel: PartyListViewModel, onDismissRequest: () -> Unit) {
    var removing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                Text(stringResource(R.string.party_leave_confirmation))
            }
        },
        dismissButton = {
            TextButton(
                enabled = !removing,
                onClick = onDismissRequest
            ) {
                Text(stringResource(R.string.button_cancel).toUpperCase(Locale.current))
            }
        },
        confirmButton = {
            val coroutineScope = rememberCoroutineScope()
            val userId = UserId.fromString(AmbientUser.current.id)

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
                Text(stringResource(R.string.button_leave).toUpperCase(Locale.current))
            }
        }
    )
}
