package cz.frantisekmasa.wfrp_master.common.core.ui.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CompendiumButton(modifier: Modifier, onClick: () -> Unit) {
    OutlinedButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Box(Modifier.padding(end = Spacing.small)) {
            Icon(
                drawableResource(Resources.Drawable.Compendium),
                VisualOnlyIconDescription,
                Modifier.size(16.dp)
            )
        }
        Text(stringResource(Str.compendium_title))
    }
}
