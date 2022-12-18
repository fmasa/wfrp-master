package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun EncumbranceBox(trapping: InventoryItem) {
    Column {
        val totalEncumbrance = trapping.totalEncumbrance
        val effectiveEncumbrance = trapping.effectiveEncumbrance

        SingleLineTextValue(
            LocalStrings.current.trappings.labelEncumbranceTotal,
            buildAnnotatedString {
                if (totalEncumbrance != effectiveEncumbrance) {
                    withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) {
                        append(totalEncumbrance.toString())
                    }

                    append(" ➔ ")
                }

                append(effectiveEncumbrance.toString())
            },
        )

        SingleLineTextValue(
            LocalStrings.current.trappings.labelEncumbrancePerUnit,
            trapping.encumbrance.toString(),
        )
    }
}
