package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import cz.frantisekmasa.wfrp_master.core.media.LocalSoundEnabled
import cz.frantisekmasa.wfrp_master.core.ui.theme.SystemBarsChangingEffect
import cz.frantisekmasa.wfrp_master.core.viewModel.provideSettingsViewModel

class Theme {
    class FixedColors(
        val danger: Color,
        val splashScreenContent: Color,
    )

    companion object {
        val fixedColors = FixedColors(
            danger = Color(183, 28, 28),
            splashScreenContent = Color(234, 234, 234),
        )

        @Composable
        internal fun LightColors() = lightColors(
            primary = Color(183, 28, 28),
            primaryVariant = Color(183, 28, 28),
            secondary = Color(183,28,28),
            secondaryVariant = Color(183,28,28),
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
        internal fun DarkColors() = darkColors(
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
    val viewModel = provideSettingsViewModel()
    val darkMode = viewModel.darkMode.observeAsState().value ?: isSystemInDarkTheme()
    val soundEnabled = viewModel.soundEnabled.observeAsState().value ?: false

    MaterialTheme(
        colors = if (darkMode) Theme.DarkColors() else Theme.LightColors(),
        typography = MaterialTheme.typography.copy(
            caption = MaterialTheme.typography.caption.copy(fontSize = 14.sp),
        ),
    ) {
        SystemBarsChangingEffect()
        CompositionLocalProvider(LocalSoundEnabled provides soundEnabled) {
            content()
        }
    }
}
