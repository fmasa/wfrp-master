package cz.frantisekmasa.wfrp_master.common.invitation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.PersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.invitation.domain.InvitationProcessingResult
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@Composable
fun InvitationConfirmation(
    invitation: Invitation,
    screenModel: InvitationScreenModel,
    onError: () -> Unit,
    onSuccess: () -> Unit,
) {
    var processingInvitation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (processingInvitation) {
        FullScreenProgress()
        return
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(),
    ) {
        Text(
            text =
                stringResource(
                    Str.parties_messages_invitation_confirmation,
                    invitation.partyName,
                ),
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        val userId = LocalUser.current.id
        val snackbarHolder = LocalPersistentSnackbarHolder.current
        val errorInvalidInvitationToken =
            stringResource(
                Str.parties_messages_invalid_invitation_token,
            )
        val errorAlreadyMember = stringResource(Str.parties_messages_already_member)

        PrimaryButton(
            stringResource(Str.parties_button_join).uppercase(),
            onClick = {
                processingInvitation = true

                coroutineScope.launch {
                    when (val result = screenModel.acceptInvitation(userId, invitation)) {
                        InvitationProcessingResult.Success -> {
                            onSuccess()
                            return@launch
                        }
                        is InvitationProcessingResult.InvalidInvitation -> {
                            showError(
                                errorInvalidInvitationToken,
                                snackbarHolder,
                                result.cause,
                            )

                            onError()
                        }
                        InvitationProcessingResult.AlreadyInParty -> {
                            showError(errorAlreadyMember, snackbarHolder)
                            onError()
                        }
                    }
                }
            },
        )
    }
}

private fun showError(
    message: String,
    snackbarHolder: PersistentSnackbarHolder,
    cause: Throwable? = null,
) {
    snackbarHolder.showSnackbar(message, SnackbarDuration.Short)
    Napier.i("Invitation processing error: $message", cause)
}
