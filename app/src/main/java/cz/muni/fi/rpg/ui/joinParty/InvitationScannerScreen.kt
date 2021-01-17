package cz.muni.fi.rpg.ui.joinParty

import android.Manifest
import android.os.Parcelable
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.activity
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.party.Invitation
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.viewModels.provideJoinPartyViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Composable
fun InvitationScannerScreen(routing: Routing<Route>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.qr_scan_prompt)) },
                navigationIcon = {
                    BackButton(onClick = { routing.pop() })
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
            val viewModel = provideJoinPartyViewModel()
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

                                routing.pop()
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
                is InvitationScannerScreenState.WaitingForUserConfirmation -> {
                    InvitationConfirmation(
                        state.invitation,
                        viewModel,
                        onSuccess = {
                            routing.pop()
                        },
                        onError = {
                            screenState = InvitationScannerScreenState.Scanning
                        },
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
    data class WaitingForUserConfirmation(val invitation: Invitation) : InvitationScannerScreenState()
}

private const val PermissionRequestCode = 10