package cz.frantisekmasa.wfrp_master.common.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon

@Composable
fun CharacterAvatar(url: String?, size: ItemIcon.Size, modifier: Modifier = Modifier) {
    Box(modifier) {
        when (url) {
            null -> ItemIcon(Resources.Drawable.DefaultAvatarIcon, size = size)
            else -> ItemIcon(url, size = size)
        }
    }
}
