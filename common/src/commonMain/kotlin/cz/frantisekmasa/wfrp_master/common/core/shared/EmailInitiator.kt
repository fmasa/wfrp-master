package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable

@Composable
expect fun rememberEmailInitiator(): EmailInitiator

fun interface EmailInitiator {
    fun initiateNewEmail(subject: String, recipient: String)
}