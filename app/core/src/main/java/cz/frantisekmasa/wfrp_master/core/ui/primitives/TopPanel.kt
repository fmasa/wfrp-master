package cz.frantisekmasa.wfrp_master.core.ui.primitives

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun TopPanel(content: @Composable () -> Unit) {
    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Spacing.small),
            elevation = 2.dp,
            shape = RectangleShape,
            content = content,
        )
    }
}