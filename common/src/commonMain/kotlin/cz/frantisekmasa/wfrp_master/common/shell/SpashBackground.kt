package cz.frantisekmasa.wfrp_master.common.shell

import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.material.primarySurface
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

fun Modifier.splashBackground() =
    composed {
        val darkMode = !MaterialTheme.colors.isLight

        if (darkMode) {
            background(MaterialTheme.colors.primarySurface)
        } else {
            background(
                Brush.verticalGradient(
                    listOf(Color(181, 12, 15), Color(138, 11, 14)),
                ),
            )
        }
    }
