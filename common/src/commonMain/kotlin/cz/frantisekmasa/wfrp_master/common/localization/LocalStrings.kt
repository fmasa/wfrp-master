package cz.frantisekmasa.wfrp_master.common.localization

import androidx.compose.runtime.staticCompositionLocalOf

object Localization {
    val English = Strings()
}

val LocalStrings = staticCompositionLocalOf { Localization.English }
