package cz.muni.fi.rpg.ui.joinParty

import android.content.Context
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.model.domain.invitation.AlreadyInParty
import cz.muni.fi.rpg.model.domain.invitation.InvalidInvitation
import cz.muni.fi.rpg.viewModels.JoinPartyViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun InvitationConfirmation(
    invitation: Invitation,
    viewModel: JoinPartyViewModel,
    onError: () -> Unit,
    onSuccess: () -> Unit,
) {
    var processingInvitation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (processingInvitation) {
        FullScreenProgress()
        return
    }

    val strings = LocalStrings.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(
            text = strings.parties.messages.invitationConfirmation(invitation.partyName),
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        val userId = LocalUser.current.id
        val context = LocalContext.current

        PrimaryButton(
            strings.parties.buttonJoin,
            onClick = {
                processingInvitation = true


                coroutineScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            viewModel.acceptInvitation(userId, invitation)
                        }

                        withContext(Dispatchers.Main) { onSuccess() }
                    } catch (e: Throwable) {
                        when (e) {
                            is InvalidInvitation -> {
                                showError(e, context, strings.parties.messages.invalidInvitationToken)
                            }
                            is AlreadyInParty -> {
                                showError(
                                    e,
                                    context,
                                    strings.parties.messages.alreadyMember,
                                )
                            }
                            is CancellationException -> {
                            }
                            else -> {
                                showError(e, context, strings.messages.errorUnknown)
                                Napier.e(e.toString(), e)
                            }
                        }

                        onError()
                    }
                }
            }
        )
    }
}

@MainThread
private fun showError(
    e: Throwable,
    context: Context,
    message: String,
) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    Napier.i("Invitation processing error: ${e.message}", e)
}
