package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import cz.frantisekmasa.wfrp_master.core.media.AmbientSoundEnabled
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.shell.AmbientSystemUiController
import cz.frantisekmasa.wfrp_master.core.viewModel.provideSettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Theme {
    class FixedColors(
        val primaryDark: Color,
        val danger: Color,
        val splashScreenContent: Color,
    )

    companion object {
        val fixedColors = FixedColors(
            primaryDark =  Color(167, 20, 20),
            danger = Color(183, 28, 28),
            splashScreenContent = Color(234, 234, 234),
        )

        @Composable
        internal fun LightColors() = lightColors(
            primary = Color(183, 28, 28),
            primaryVariant = Color(183, 28, 28),
            secondary = colorResource(R.color.colorPrimary),
            secondaryVariant = colorResource(R.color.colorPrimary),
            background = colorResource(R.color.colorBackgroundUnderCard),
            surface = colorResource(R.color.colorCardBackground),
            error = colorResource(R.color.colorDanger),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.Black,
            onSurface = Color.Black,
            onError = Color.White,
        )

        @Composable
        internal fun DarkColors() = darkColors(
            primary = Color(239, 154, 154),
            primaryVariant = Color(239, 154, 154),
            secondary = Color(183, 28, 28),
            background = Color(18, 18, 18),
            surface = Color(18, 18, 18),
            error = colorResource(R.color.colorDanger),
            onPrimary = Color.Black,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onError = Color.White,
        )
    }
}

private val darkSystemColor = Color(35, 35, 35)

@Composable
fun Theme(content: @Composable () -> Unit) {
    val viewModel = provideSettingsViewModel()
    val darkMode = viewModel.darkMode.observeAsState().value ?: isSystemInDarkTheme()
    val soundEnabled = viewModel.soundEnabled.observeAsState().value ?: false

    val colors = if (darkMode) Theme.DarkColors() else Theme.LightColors()
    val systemUi = AmbientSystemUiController.current

    LaunchedEffect(colors.isLight, systemUi) {
        withContext(Dispatchers.Main) {
            systemUi.setStatusBarColor(
                if (colors.isLight) Theme.fixedColors.primaryDark else darkSystemColor
            )

            systemUi.setNavigationBarColor(
                if (colors.isLight) Color(235, 235, 235) else darkSystemColor
            )
        }
    }

    MaterialTheme(
        colors = if (darkMode) Theme.DarkColors() else Theme.LightColors(),
        typography = MaterialTheme.typography.copy(
            caption = MaterialTheme.typography.caption.copy(fontSize = 14.sp),
        ),
    ) {
        Providers(AmbientSoundEnabled provides soundEnabled) {
            content()
        }
    }
}
