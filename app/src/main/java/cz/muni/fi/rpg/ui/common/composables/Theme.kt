package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import cz.frantisekmasa.wfrp_master.core.media.AmbientSoundEnabled
import cz.frantisekmasa.wfrp_master.core.ui.shell.AmbientSystemUiController
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.SettingsViewModel

class Theme {
    class FixedColors(
        val danger: Color,
        val currencyGold: Color,
        val currencySilver: Color,
        val currencyBrass: Color,
    )

    companion object {
        val fixedColors = FixedColors(
            danger = Color(183, 28, 28),
            currencyGold = Color(255, 183, 77),
            currencySilver = Color(158, 158, 158),
            currencyBrass = Color(141, 110, 99),
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
    val viewModel: SettingsViewModel by viewModel()
    val darkMode by viewModel.darkMode.collectAsState(false)
    val soundEnabled by viewModel.soundEnabled.collectAsState(false)

    val colors = if (darkMode) Theme.DarkColors() else Theme.LightColors()
    val systemUi = AmbientSystemUiController.current

    onCommit(colors.isLight, systemUi) {
        systemUi.setStatusBarColor(
            if (colors.isLight) Color(167, 20, 20) else darkSystemColor
        )

        systemUi.setNavigationBarColor(
            if (colors.isLight) Color(235, 235, 235) else darkSystemColor
        )
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