package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun EncumbranceBox(trapping: InventoryItem) {
    EncumbranceBox(
        encumbrance = trapping.encumbrance,
        characterTrapping = trapping,
    )
}

@Composable
fun EncumbranceBox(
    encumbrance: Encumbrance,
    characterTrapping: InventoryItem?,
) {
    Column {
        if (characterTrapping != null) {
            val totalEncumbrance = characterTrapping.totalEncumbrance
            val effectiveEncumbrance = characterTrapping.effectiveEncumbrance

            SingleLineTextValue(
                stringResource(Str.trappings_label_encumbrance_total),
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
        }

        SingleLineTextValue(
            stringResource(Str.trappings_label_encumbrance_per_unit),
            encumbrance.toString(),
        )
    }
}
