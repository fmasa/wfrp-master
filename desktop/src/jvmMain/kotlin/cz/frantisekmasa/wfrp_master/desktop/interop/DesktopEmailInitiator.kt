package cz.frantisekmasa.wfrp_master.desktop.interop

import cz.frantisekmasa.wfrp_master.common.core.shared.EmailInitiator
import io.ktor.http.formUrlEncode
import java.awt.Desktop
import java.net.URI

object DesktopEmailInitiator : EmailInitiator {
    override fun initiateNewEmail(subject: String, recipient: String) {
        if (!Desktop.isDesktopSupported()) {
            return
        }

        val desktop: Desktop = Desktop.getDesktop()

        if (desktop.isSupported(Desktop.Action.MAIL)) {
            desktop.mail(URI("mailto:$recipient?" + listOf("subject" to subject).formUrlEncode()))
        }
    }
}
