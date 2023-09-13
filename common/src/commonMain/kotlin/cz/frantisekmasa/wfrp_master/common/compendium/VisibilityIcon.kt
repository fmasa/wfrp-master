package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.CompendiumItem
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun VisibilityIcon(item: CompendiumItem<*>) {
    VisibilityIcon(item.isVisibleToPlayers)
}

@Composable
fun VisibilityIcon(isVisibleToPlayers: Boolean) {
    val modifier = Modifier.size(16.dp)

    if (isVisibleToPlayers) {
        Icon(
            Icons.Rounded.Visibility,
            stringResource(Str.compendium_visible_to_players_true),
            modifier = modifier,
        )
    } else {
        Icon(
            Icons.Rounded.VisibilityOff,
            stringResource(Str.compendium_visible_to_players_false),
            modifier = modifier,
        )
    }
}
