package cz.frantisekmasa.wfrp_master.common.core.ui.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable

@Composable
fun StickyHeader(content: @Composable () -> Unit) {
    Surface {
        Column {
            content()
            Divider()
        }
    }
}
