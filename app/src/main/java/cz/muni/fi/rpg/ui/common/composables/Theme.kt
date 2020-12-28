package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
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

@Composable
fun Theme(content: @Composable () -> Unit) {
    val viewModel: SettingsViewModel by viewModel()
    val darkMode by viewModel.darkMode.collectAsState(false)

    MaterialTheme(
        colors = if (darkMode) Theme.DarkColors() else Theme.LightColors(),
        typography = MaterialTheme.typography.copy(
            caption = MaterialTheme.typography.caption.copy(fontSize = 14.sp),
        ),
        content = content
    )
}