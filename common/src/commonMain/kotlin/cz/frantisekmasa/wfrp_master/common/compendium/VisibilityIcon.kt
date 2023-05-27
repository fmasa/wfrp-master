package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun VisibilityIcon(item: CompendiumItem<*>) {
    val modifier = Modifier.size(16.dp)

    if (item.isVisibleToPlayers) {
        Icon(
            Icons.Rounded.Visibility,
            LocalStrings.current.compendium.visibleToPlayersTrue,
            modifier = modifier,
        )
    } else {
        Icon(
            Icons.Rounded.VisibilityOff,
            LocalStrings.current.compendium.visibleToPlayersFalse,
            modifier = modifier,
        )
    }
}
