package cz.muni.fi.rpg.ui.partyList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.longToast
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.PartyListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RemovePartyDialog(party: Party, viewModel: PartyListViewModel, onDismissRequest: () -> Unit) {
    var removing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.medium)) {
                Text(stringResource(R.string.party_remove_confirmation))

                if (party.users.size > 1) {
                    Text(
                        stringResource(R.string.party_remove_multiple_members),
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
                Text(stringResource(R.string.button_cancel).toUpperCase(Locale.current))
            }
        },
        confirmButton = {
            val coroutineScope = rememberCoroutineScope()
            val context = AmbientContext.current

            TextButton(
                enabled = !removing,
                onClick = {
                    removing = true
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.archive(party.id)

                        withContext(Dispatchers.Main) {
                            onDismissRequest()
                            longToast(context, R.string.message_party_removed)
                        }
                    }
                },
            ) {
                Text(stringResource(R.string.button_remove).toUpperCase(Locale.current))
            }
        }
    )
}
