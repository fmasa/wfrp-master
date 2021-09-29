package cz.frantisekmasa.wfrp_master.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.core.R
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon

@Composable
fun CharacterAvatar(url: String?, size: ItemIcon.Size, modifier: Modifier = Modifier) {
    Box(modifier) {
        when (url) {
            null -> ItemIcon(R.drawable.ic_face, size = size)
            else -> ItemIcon(url, size = size)
        }
    }
}
