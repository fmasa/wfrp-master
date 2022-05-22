package cz.frantisekmasa.wfrp_master.common.character.combat

import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Flaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Quality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Rating
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingFeature
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Composable
fun <Q : Quality, F : Flaw> TrappingFeatureList(
    qualities: Map<Q, Rating>,
    flaws: Map<F, Rating>,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        val strings = LocalStrings.current

        Text(
            remember(qualities, flaws) {
                (translate(qualities, strings).sorted() + translate(flaws, strings).sorted())
                    .joinToString(", ")
            },
            modifier = modifier,
            style = MaterialTheme.typography.body2
        )
    }
}

private fun <T : TrappingFeature> translate(
    features: Map<T, Rating>,
    strings: Strings,
): List<String> {
    return features.map { (feature, rating) ->
        val name = feature.nameResolver(strings)
        if (feature.hasRating) "$name $rating" else name
    }
}