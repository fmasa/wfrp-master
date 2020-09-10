package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import cz.muni.fi.rpg.R

class Theme {
    companion object {

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
            primary = Color(183, 28, 28),
            primaryVariant = Color(183, 28, 28),
            secondary = colorResource(R.color.colorPrimary),
            background = Color.DarkGray,
            surface = Color.Black,
            error = colorResource(R.color.colorDanger),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            onError = Color.White,
        )
    }
}

@Composable
fun Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = Theme.LightColors(),
        typography = MaterialTheme.typography.copy(
            caption = MaterialTheme.typography.caption.copy(fontSize = 14.sp),
        ),
        content = content
    )
}