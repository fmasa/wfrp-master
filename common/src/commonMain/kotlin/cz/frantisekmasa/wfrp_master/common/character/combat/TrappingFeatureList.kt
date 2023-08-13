package cz.frantisekmasa.wfrp_master.common.character.combat

import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Flaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Quality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Rating
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingFeature

@Composable
fun <Q : Quality, F : Flaw> TrappingFeatureList(
    qualities: Map<Q, Rating>,
    flaws: Map<F, Rating>,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        val translatedQualities = translateFeatures(qualities)
        val translatedFlaws = translateFeatures(flaws)

        Text(
            remember(translatedQualities, translatedFlaws) {
                (translatedQualities.sorted() + translatedFlaws.sorted())
                    .joinToString(", ")
            },
            modifier = modifier,
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
@Stable
fun <T : TrappingFeature> translateFeatures(
    features: Map<T, Rating>,
): List<String> {
    return features.map { (feature, rating) ->
        val name = feature.localizedName

        if (!feature.hasRating) {
            return@map name
        }

        val ratingUnit = feature.ratingUnit
        val formattedRating = if (ratingUnit != null) "($rating$ratingUnit)" else rating.toString()

        "$name $formattedRating"
    }
}
