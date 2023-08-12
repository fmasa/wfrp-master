package cz.frantisekmasa.wfrp_master.common.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.ui.material.SetupMaterialRichText
import cz.frantisekmasa.wfrp_master.common.core.shared.LocalSoundEnabled
import cz.frantisekmasa.wfrp_master.common.core.shared.SettingsStorage
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.settings.AppSettings
import org.kodein.di.compose.localDI
import org.kodein.di.instance

class Theme {
    class FixedColors(
        val splashScreenContent: Color,
        val warning: Color,
        // TODO: Use this for primaryVariant
        val accent: Color,
    )

    companion object {
        val fixedColors = FixedColors(
            splashScreenContent = Color(234, 234, 234),
            warning = Color(255, 214, 154),
            accent = Color(83, 109, 254),
        )

        @Composable
        internal fun lightColors() = androidx.compose.material.lightColors(
            primary = Color(183, 28, 28),
            primaryVariant = Color(183, 28, 28),
            secondary = Color(183, 28, 28),
            secondaryVariant = Color(183, 28, 28),
            background = Color(250, 250, 250),
            surface = Color(255, 255, 255),
            error = Color(183, 28, 28),
            onPrimary = Color(255, 255, 255),
            onSecondary = Color(255, 255, 255),
            onBackground = Color(0, 0, 0),
            onSurface = Color(0, 0, 0),
            onError = Color(255, 255, 255),
        )

        @Composable
        internal fun darkColors() = androidx.compose.material.darkColors(
            primary = Color(239, 154, 154),
            primaryVariant = Color(239, 154, 154),
            secondary = Color(183, 28, 28),
            background = Color(18, 18, 18),
            surface = Color(18, 18, 18),
            error = Color(183, 28, 28),
            onPrimary = Color.Black,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onError = Color.White,
        )
    }
}

@Composable
fun Theme(content: @Composable () -> Unit) {
    val settings: SettingsStorage by localDI().instance()

    val darkMode = settings.watch(AppSettings.DARK_MODE).collectWithLifecycle(null).value ?: isSystemInDarkTheme()
    val soundEnabled = settings.watch(AppSettings.SOUND_ENABLED).collectWithLifecycle(null).value ?: false

    MaterialTheme(
        colors = if (darkMode) Theme.darkColors() else Theme.lightColors(),
        typography = MaterialTheme.typography.copy(
            caption = MaterialTheme.typography.caption.copy(fontSize = 14.sp),
            overline = MaterialTheme.typography.overline.copy(
                fontSize = 12.sp,
                letterSpacing = 0.25.sp,
            )
        ),
    ) {
        SystemBarsChangingEffect()
        CompositionLocalProvider(
            LocalSoundEnabled provides soundEnabled,
        ) {
            SetupMaterialRichText {
                content()
            }
        }
    }
}
