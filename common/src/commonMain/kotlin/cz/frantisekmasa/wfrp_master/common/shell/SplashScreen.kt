package cz.frantisekmasa.wfrp_master.common.shell

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.theme.Theme
import cz.frantisekmasa.wfrp_master.common.localization.FixedStrings

@Composable
fun SplashScreen(content: (@Composable () -> Unit)? = null) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .splashBackground()
                .padding(Spacing.bodyPadding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.small, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    drawableResource(Resources.Drawable.SplashScreenIcon),
                    FixedStrings.APP_NAME,
                    Modifier.size(140.dp),
                )
                Text(
                    FixedStrings.APP_NAME,
                    style = MaterialTheme.typography.h6,
                    color = Theme.fixedColors.splashScreenContent,
                )
            }
            content?.invoke()
        }
    }
}
