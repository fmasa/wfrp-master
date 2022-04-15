package cz.frantisekmasa.wfrp_master.desktop.interop

import cz.frantisekmasa.wfrp_master.common.core.shared.UrlOpener
import java.awt.Desktop
import java.net.URI

object DesktopUrlOpener : UrlOpener {
    override fun open(url: String, isGooglePlayLink: Boolean) {
        if (!Desktop.isDesktopSupported()) {
            return
        }

        val desktop: Desktop = Desktop.getDesktop()

        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            desktop.browse(URI(url))
        }
    }
}
