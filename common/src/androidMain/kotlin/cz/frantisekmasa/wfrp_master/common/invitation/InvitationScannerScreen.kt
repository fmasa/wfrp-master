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
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.party.Invitation
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch

actual class InvitationScannerScreen : Screen {
    @Composable
    override fun Content() {
        val navigation = LocalNavigationTransaction.current

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Str.parties_title_join)) },
                    navigationIcon = { BackButton() },
                )
            },
            modifier = Modifier.fillMaxHeight(),
        ) { contentPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight().padding(contentPadding),
            ) {
                val screenModel: InvitationScreenModel = rememberScreenModel()

                val (invitation, setInvitation) =
                    rememberSaveable {
                        mutableStateOf<Invitation?>(null)
                    }

                when {
                    invitation != null -> {
                        InvitationConfirmation(
                            invitation,
                            screenModel,
                            onSuccess = navigation::goBack,
                            onError = { setInvitation(null) },
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
    private fun Scanner(
        screenModel: InvitationScreenModel,
        onSuccessfulScan: (Invitation) -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val camera = rememberPermissionState(Manifest.permission.CAMERA)

        when {
            camera.status.isGranted -> {
                SubheadBar(stringResource(Str.parties_messages_qr_code_scanning_prompt))
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
            camera.status.shouldShowRationale -> PermissionRequestScreen(camera)
            else -> PermissionDeniedScreen()
        }
    }

    @Composable
    private fun PermissionRequestScreen(camera: PermissionState) {
        ScreenBody {
            SideEffect { camera.launchPermissionRequest() }

            Text(
                stringResource(Str.permissions_camera_required),
                style = MaterialTheme.typography.h6,
            )

            Rationale()

            TextButton(onClick = { camera.launchPermissionRequest() }) {
                Text(stringResource(Str.permissions_button_request_permission).uppercase())
            }

            Alternative()
        }
    }

    @Composable
    private fun PermissionDeniedScreen() {
        ScreenBody {
            Text(
                stringResource(Str.permissions_camera_denied),
                style = MaterialTheme.typography.h6,
            )
            Rationale()
            Text(
                stringResource(Str.permissions_messages_settings_screen_instructions),
                textAlign = TextAlign.Center,
            )

            val context = LocalContext.current
            TextButton(onClick = { context.openApplicationSettings() }) {
                Text(stringResource(Str.permissions_button_open_settings).uppercase())
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
            stringResource(Str.permissions_messages_camera_permission_rationale),
            textAlign = TextAlign.Center,
        )
    }

    @Composable
    private fun Alternative() {
        HorizontalLine()

        Text(
            stringResource(Str.parties_messages_invitation_link_alternative),
            modifier = Modifier.padding(top = Spacing.mediumLarge),
            textAlign = TextAlign.Center,
        )
    }

    private fun Context.openApplicationSettings() {
        startActivity(
            Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            },
        )
    }
}
