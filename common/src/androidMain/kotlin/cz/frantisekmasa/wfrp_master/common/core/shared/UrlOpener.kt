package cz.frantisekmasa.wfrp_master.common.core.shared

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberUrlOpener(): UrlOpener {
    val context = LocalContext.current

    return UrlOpener { url, isGooglePlayLink ->
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                if (isGooglePlayLink) {
                    setPackage("com.android.vending")
                }
            }
        )
    }
}