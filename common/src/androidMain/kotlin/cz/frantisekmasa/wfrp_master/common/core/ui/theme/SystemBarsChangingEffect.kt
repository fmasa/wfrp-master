package cz.frantisekmasa.wfrp_master.common.core.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val darkSystemColor = Color(35, 35, 35)
private val lightSystemColor = Color(167, 20, 20)
private val lightNavigationBarColor = Color(235, 235, 235)

/**
 * Changes color of navigation bar and status bar according to current theme
 */
@Composable
actual fun SystemBarsChangingEffect() {
    val systemUi = LocalSystemUiController.current
    val colors = MaterialTheme.colors

    LaunchedEffect(colors.isLight, systemUi) {
        withContext(Dispatchers.Main) {
            systemUi.setStatusBarColor(
                if (colors.isLight) lightSystemColor else darkSystemColor
            )

            systemUi.setNavigationBarColor(
                if (colors.isLight) lightNavigationBarColor else darkSystemColor
            )
        }
    }
}
