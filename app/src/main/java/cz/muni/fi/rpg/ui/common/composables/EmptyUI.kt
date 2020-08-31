package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.TextUnit
import cz.muni.fi.rpg.R

@Composable
fun EmptyUI(@StringRes textId: Int, @DrawableRes drawableResourceId: Int) {
    val image = vectorResource(drawableResourceId)
    val color = colorResource(R.color.colorEmptyState)
    val text = stringResource(textId)

    Column(horizontalGravity = Alignment.CenterHorizontally,) {
        Spacer(Modifier.fillMaxHeight(0.35f))
        Image(
            image,
            modifier = Modifier
                .fillMaxWidth(0.30f),
            colorFilter = ColorFilter.tint(color)
        )

        Text(
            text,
            style = MaterialTheme.typography.subtitle1,
            color = color,
            fontSize = TextUnit.Sp(18)
        )
    }
}