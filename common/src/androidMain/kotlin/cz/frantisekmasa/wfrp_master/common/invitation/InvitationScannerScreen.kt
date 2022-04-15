package cz.frantisekmasa.wfrp_master.common.invitation

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
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.launch

actual class InvitationScannerScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(LocalStrings.current.parties.titleJoin) },
                    navigationIcon = {
                        BackButton(onClick = { navigator.pop() })
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
                val screenModel: InvitationScreenModel = rememberScreenModel()

                val (invitation, setInvitation) = rememberSaveable {
                    mutableStateOf<Invitation?>(null)
                }

                when {
                    invitation != null -> {
                        InvitationConfirmation(
                            invitation,
                            screenModel,
                            onSuccess = { navigator.pop() },
                            onError = { setInvitation(invitation) },
                        )
                    }
                    else -> {
                        Scanner(screenModel, onSuccessfulScan = setInvitation)
                    }
                }
            }
        }
    }

    @Composable
    private fun Scanner(screenModel: InvitationScreenModel, onSuccessfulScan: (Invitation) -> Unit) {
        val coroutineScope = rememberCoroutineScope()
        val camera = rememberPermissionState(Manifest.permission.CAMERA)

        when {
            camera.hasPermission -> {
                SubheadBar(LocalStrings.current.parties.messages.qrCodeScanningPrompt)
                QrCodeScanner(
                    modifier = Modifier.fillMaxSize(),
                    onSuccessfulScan = { qrCodeData ->
                        coroutineScope.launch {
                            screenModel.deserializeInvitation(qrCodeData)
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

            val strings = LocalStrings.current.permissions

            Text(
                strings.cameraRequired,
                style = MaterialTheme.typography.h6,
            )

            Rationale()

            TextButton(onClick = { camera.launchPermissionRequest() }) {
                Text(strings.buttonRequestPermission.uppercase())
            }

            Alternative()
        }
    }

    @Composable
    private fun PermissionDeniedScreen() {
        ScreenBody {
            val strings = LocalStrings.current.permissions

            Text(strings.cameraDenied, style = MaterialTheme.typography.h6)
            Rationale()
            Text(strings.messages.settingsScreenInstructions, textAlign = TextAlign.Center)

            val context = LocalContext.current
            TextButton(onClick = { context.openApplicationSettings() }) {
                Text(strings.buttonOpenSettings.uppercase())
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
            LocalStrings.current.permissions.messages.cameraPermissionRationale,
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    private fun Alternative() {

        HorizontalLine()

        Text(
            LocalStrings.current.parties.messages.invitationLinkAlternative,
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
}