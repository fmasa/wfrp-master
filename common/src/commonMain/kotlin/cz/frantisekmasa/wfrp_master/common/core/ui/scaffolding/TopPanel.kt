package cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun TopPanel(content: @Composable () -> Unit) {
    Box {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.small),
            elevation = 2.dp,
            shape = RectangleShape,
            content = content,
        )
    }
}
