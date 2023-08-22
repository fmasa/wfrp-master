package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun TrappingDescription(trapping: InventoryItem) {
    TrappingDescription(trapping.description)
}

@Composable
fun TrappingDescription(description: String) {
    RichText(Modifier.padding(top = Spacing.small)) {
        Markdown(description)
    }
}
