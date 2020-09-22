package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalLine(modifier: Modifier = Modifier) {
    Divider(
        color = EmphasisAmbient.current.disabled.applyEmphasis(MaterialTheme.colors.onBackground),
        thickness = (2f / DensityAmbient.current.density).dp,
        modifier = modifier.padding(top = 20.dp)
    )
}
