package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.combat.translateFeatures
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Flaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Quality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Rating
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TrappingFeatures(
    qualities: Map<out Quality, Rating>,
    flaws: Map<out Flaw, Rating>,
) {
    if (qualities.isNotEmpty()) {
        SingleLineTextValue(
            stringResource(Str.trappings_label_qualities),
            translateFeatures(qualities).joinToString(", "),
        )
    } else {
        SingleLineTextValue(stringResource(Str.trappings_label_qualities), none())
    }

    if (flaws.isNotEmpty()) {
        SingleLineTextValue(
            stringResource(Str.trappings_label_flaws),
            translateFeatures(flaws).joinToString(", "),
        )
    } else {
        SingleLineTextValue(stringResource(Str.trappings_label_flaws), none())
    }
}

@Composable
@Stable
private fun none() =
    AnnotatedString(
        stringResource(Str.trappings_none),
        SpanStyle(fontStyle = FontStyle.Italic),
    )
