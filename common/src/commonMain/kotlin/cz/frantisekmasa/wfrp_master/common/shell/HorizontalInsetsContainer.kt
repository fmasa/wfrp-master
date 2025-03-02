package cz.frantisekmasa.wfrp_master.common.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.layout.windowInsetsStartWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun HorizontalInsetsContainer(content: @Composable () -> Unit) {
    Row {
        Box(
            Modifier.background(Color.Black)
                .windowInsetsStartWidth(WindowInsets.displayCutout)
                .fillMaxHeight(),
        )

        content()

        Box(
            Modifier.background(Color.Black)
                .windowInsetsEndWidth(WindowInsets.displayCutout)
                .fillMaxHeight(),
        )
    }
}
