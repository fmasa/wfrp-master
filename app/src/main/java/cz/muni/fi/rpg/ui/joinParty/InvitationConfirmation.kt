package cz.muni.fi.rpg.ui.joinParty

import android.content.Context
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.text.bold
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.R
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight()
    ) {
        Text(
            text = SpannableStringBuilder()
                .append(stringResource(R.string.join_party_dialog_party_name))
                .append(" ")
                .bold { append(invitation.partyName) }
                .append(".")
                .toString(),
            style = MaterialTheme.typography.h5,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        val userId = LocalUser.current.id
        val context = LocalContext.current

        PrimaryButton(
            LocalStrings.current.parties.buttonJoin,
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
                                showError(e, context, R.string.error_invalid_invitation)
                            }
                            is AlreadyInParty -> {
                                showError(
                                    e,
                                    context,
                                    R.string.error_already_party_member
                                )
                            }
                            is CancellationException -> {
                            }
                            else -> {
                                showError(e, context, R.string.error_unkown)
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
    @StringRes messageRes: Int,
) {
    Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
    Napier.i("Invitation processing error: ${e.message}", e)
}
