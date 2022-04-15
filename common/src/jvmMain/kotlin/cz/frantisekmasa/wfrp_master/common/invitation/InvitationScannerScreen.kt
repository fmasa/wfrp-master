package cz.frantisekmasa.wfrp_master.common.invitation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

actual class InvitationScannerScreen : Screen {
    @Composable
    override fun Content() {
        error("Desktop does not support invitation QR code scanning")
    }
}
