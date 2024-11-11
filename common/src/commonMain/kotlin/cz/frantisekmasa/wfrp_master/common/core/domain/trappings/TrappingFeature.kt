package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import dev.icerock.moko.parcelize.Parcelable

interface TrappingFeature : NamedEnum, Parcelable {
    val hasRating: Boolean
    val ratingUnit: String? get() = null

    @Stable
    @Composable
    fun formatValue(rating: Int): String {
        val name = localizedName

        if (!hasRating) {
            return name
        }

        val ratingUnit = ratingUnit
        val formattedRating = if (ratingUnit != null) "($rating$ratingUnit)" else rating.toString()

        return "$name $formattedRating"
    }
}
