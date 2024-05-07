package cz.frantisekmasa.wfrp_master.common.core.ui.text

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText

@Composable
fun MarkdownTextValue(
    label: String,
    value: String,
) {
    if (value.isBlank()) return

    Column {
        Text(
            "$label:",
            fontWeight = FontWeight.Bold,
        )

        RichText {
            Markdown(value)
        }
    }
}
