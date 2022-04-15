package cz.frantisekmasa.wfrp_master.common.core.shared

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberEmailInitiator(): EmailInitiator {
    val context = LocalContext.current

    return EmailInitiator { subject, recipient ->
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "plain/text"
            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(recipient)
            )
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        context.startActivity(Intent.createChooser(intent, ""))
    }
}