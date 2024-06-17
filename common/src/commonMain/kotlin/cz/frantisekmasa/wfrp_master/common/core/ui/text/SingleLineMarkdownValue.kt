package cz.frantisekmasa.wfrp_master.common.core.ui.text

import androidx.compose.runtime.Composable
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText

@Composable
fun SingleLineMarkdownValue(
    label: String,
    value: String,
) {
    if (value.isBlank()) return

    RichText {
        Markdown("**$label:** $value")
    }
}
