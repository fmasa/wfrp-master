package cz.muni.fi.rpg.ui.joinParty

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.JoinPartyViewModel
import cz.muni.fi.rpg.viewModels.provideJoinPartyViewModel
import kotlinx.coroutines.launch

@Composable
fun InvitationScannerScreen(routing: Routing<Route>) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_joinParty)) },
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

            var invitation: Invitation? by rememberSaveable { mutableStateOf(null) }

            when {
                invitation != null -> {
                    InvitationConfirmation(
                        invitation!!,
                        viewModel,
                        onSuccess = { routing.pop() },
                        onError = { invitation = null },
                    )
                }
                else -> {
                    Scanner(viewModel, onSuccessfulScan = { invitation = it })
                }
            }
        }
    }
}

@Composable
private fun Scanner(viewModel: JoinPartyViewModel, onSuccessfulScan: (Invitation) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val camera = rememberPermissionState(Manifest.permission.CAMERA)

    when {
        camera.hasPermission -> {
            SubheadBar(stringResource(R.string.qr_scan_prompt))
            QrCodeScanner(
                modifier = Modifier.fillMaxSize(),
                onSuccessfulScan = { qrCodeData ->
                    coroutineScope.launch {
                        viewModel.deserializeInvitationJson(qrCodeData)
                            ?.let(onSuccessfulScan)
                    }
                },
            )
        }
        !camera.permissionRequested || camera.shouldShowRationale -> PermissionRequestScreen(camera)
        else -> PermissionDeniedScreen()
    }
}

@Composable
private fun PermissionRequestScreen(camera: PermissionState) {
    ScreenBody {
        if (!camera.permissionRequested) {
            SideEffect { camera.launchPermissionRequest() }
        }

        Text(
            stringResource(R.string.camera_permission_required),
            style = MaterialTheme.typography.h6,
        )

        Rationale()

        TextButton(onClick = { camera.launchPermissionRequest() }) {
            Text(stringResource(R.string.button_request_permission).uppercase())
        }

        Alternative()
    }
}

@Composable
private fun PermissionDeniedScreen() {
    ScreenBody {
        Text(stringResource(R.string.camera_permission_denied), style = MaterialTheme.typography.h6)
        Rationale()
        Text(stringResource(R.string.camera_permission_instructions), textAlign = TextAlign.Center)

        val context = LocalContext.current
        TextButton(onClick = { context.openApplicationSettings() }) {
            Text(stringResource(R.string.button_open_settings).uppercase())
        }

        Alternative()
    }
}

@Composable
private inline fun ScreenBody(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(Spacing.bodyPadding)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content,
    )
}

@Composable
private fun Rationale() {
    Text(
        stringResource(R.string.camera_permission_rationale),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun Alternative() {

    HorizontalLine()

    Text(
        stringResource(R.string.camera_permission_alternative),
        modifier = Modifier.padding(top = Spacing.mediumLarge),
        textAlign = TextAlign.Center,
    )
}

private fun Context.openApplicationSettings() {
    startActivity(
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
    )
}
