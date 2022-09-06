package cz.frantisekmasa.wfrp_master.common.core.shared

import androidx.compose.runtime.Composable

@Composable
expect fun rememberUrlOpener(): UrlOpener

fun interface UrlOpener {
    fun open(url: String, isGooglePlayLink: Boolean)
}
