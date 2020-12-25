package cz.muni.fi.rpg.ui.joinParty

import android.Manifest
import android.content.Context
import android.os.Parcelable
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.text.bold
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.activity
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.invitation.AlreadyInParty
import cz.muni.fi.rpg.model.domain.invitation.InvalidInvitation
import cz.muni.fi.rpg.model.domain.party.Invitation
import cz.muni.fi.rpg.ui.common.composables.AmbientUser
import cz.muni.fi.rpg.ui.common.composables.PrimaryButton
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.viewModels.InvitationScannerViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import timber.log.Timber

@Composable
fun InvitationScannerScreen(routing: Routing<Route>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.qr_scan_prompt)) },
                navigationIcon = {
                    BackButton(onClick = { routing.backStack.pop() })
                },
            )
        },
        modifier = Modifier.fillMaxHeight(),
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {
            val viewModel: InvitationScannerViewModel by viewModel()
            val coroutineScope = rememberCoroutineScope()

            var screenState: InvitationScannerScreenState by savedInstanceState {
                InvitationScannerScreenState.WaitingForPermissions
            }

            when (val state = screenState) {
                InvitationScannerScreenState.WaitingForPermissions -> {
                    val activity = activity()

                    LaunchedEffect(null) {
                        val permissionResult = PermissionManager.requestPermissions(
                            activity,
                            PermissionRequestCode,
                            Manifest.permission.CAMERA,
                        )

                        when(permissionResult) {
                            is PermissionResult.PermissionGranted -> {
                                screenState = InvitationScannerScreenState.Scanning
                            }
                            else -> {
                                // TODO: Add more specific wording for types of denial
                                // see https://github.com/sagar-viradiya/eazypermissions#coroutines-support

                                activity.toast(
                                    activity.getString(R.string.error_camera_permission_required),
                                    Toast.LENGTH_LONG
                                )

                                routing.backStack.pop()
                            }
                        }
                    }
                }
                InvitationScannerScreenState.Scanning -> {
                    SubheadBar(stringResource(R.string.qr_scan_prompt))
                    QrCodeScanner(
                        modifier = Modifier.weight(1f),
                        onSuccessfulScan = { qrCodeData ->
                            coroutineScope.launch {
                                viewModel.deserializeInvitationJson(qrCodeData)?.let {
                                    screenState =
                                        InvitationScannerScreenState.WaitingForUserConfirmation(it)
                                }
                            }
                        },
                    )
                }
                InvitationScannerScreenState.ProcessingInvitation -> FullScreenProgress()
                is InvitationScannerScreenState.WaitingForUserConfirmation -> {
                    Text(
                        text = SpannableStringBuilder()
                            .append(stringResource(R.string.join_party_dialog_party_name))
                            .append(" ")
                            .bold { append(state.invitation.partyName) }
                            .append(".")
                            .toString(),
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val userId = AmbientUser.current.id
                    val context = AmbientContext.current

                    PrimaryButton(
                        R.string.title_joinParty,
                        onClick = {
                            val invitation = state.invitation
                            screenState = InvitationScannerScreenState.ProcessingInvitation

                            coroutineScope.launch {
                                try {
                                    withContext(Dispatchers.IO) {
                                        viewModel.acceptInvitation(userId, invitation)
                                    }

                                    withContext(Dispatchers.Main) {
                                        routing.backStack.pop()
                                    }
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
                                            Timber.e(e)
                                        }
                                    }

                                    screenState = InvitationScannerScreenState.Scanning
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

private sealed class InvitationScannerScreenState : Parcelable {
    @Parcelize
    object WaitingForPermissions : InvitationScannerScreenState()

    @Parcelize
    object Scanning : InvitationScannerScreenState()

    @Parcelize
    object ProcessingInvitation : InvitationScannerScreenState()

    @Parcelize
    data class WaitingForUserConfirmation(val invitation: Invitation) : InvitationScannerScreenState()
}

@MainThread
private fun showError(
    e: Throwable,
    context: Context,
    @StringRes messageRes: Int,
) {
    Toast.makeText(context, context.getString(messageRes), Toast.LENGTH_SHORT).show()
    Timber.i(e, "Invitation processing error: ${e.message}")
}

private const val PermissionRequestCode = 10