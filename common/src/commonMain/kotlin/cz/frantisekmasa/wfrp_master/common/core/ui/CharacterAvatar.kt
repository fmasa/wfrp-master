package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon

@Composable
fun CharacterAvatar(
    url: String?,
    size: ItemIcon.Size,
    modifier: Modifier = Modifier,
    fallback: Resources.Drawable = Resources.Drawable.DefaultAvatarIcon,
) {
    Box(modifier) {
        when (url) {
            null -> ItemIcon(fallback, size = size)
            else -> ItemIcon(url, size = size)
        }
    }
}
