package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R

@Composable
fun ItemIcon(@DrawableRes drawableResource: Int) {
    val backgroundColor = colorResource(R.color.colorPrimaryDark)

    Image(
        vectorResource(drawableResource),
        modifier = Modifier.background(backgroundColor, CircleShape).padding(12.dp)
    )
}