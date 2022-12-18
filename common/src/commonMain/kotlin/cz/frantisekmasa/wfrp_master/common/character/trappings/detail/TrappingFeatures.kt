package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import cz.frantisekmasa.wfrp_master.common.character.combat.translateFeatures
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Flaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Quality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Rating
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun TrappingFeatures(qualities: Map<out Quality, Rating>, flaws: Map<out Flaw, Rating>) {
    val strings = LocalStrings.current

    if (qualities.isNotEmpty()) {
        SingleLineTextValue(
            strings.trappings.labelQualities,
            remember(qualities) { translateFeatures(qualities, strings).joinToString(", ") }
        )
    } else {
        SingleLineTextValue(strings.trappings.labelQualities, none())
    }

    if (flaws.isNotEmpty()) {
        SingleLineTextValue(
            strings.trappings.labelFlaws,
            remember(qualities) { translateFeatures(flaws, strings).joinToString(", ") }
        )
    } else {
        SingleLineTextValue(strings.trappings.labelFlaws, none())
    }
}

@Composable
@Stable
private fun none() = AnnotatedString(
    LocalStrings.current.trappings.none,
    SpanStyle(fontStyle = FontStyle.Italic),
)
