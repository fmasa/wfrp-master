package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import cz.muni.fi.rpg.R

@Composable
fun Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = colorResource(R.color.colorPrimary),
            primaryVariant = colorResource(R.color.colorPrimary),
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
        ),
        content = content
    )
}