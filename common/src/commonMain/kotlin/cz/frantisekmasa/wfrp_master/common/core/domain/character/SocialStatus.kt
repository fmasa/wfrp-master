package cz.frantisekmasa.wfrp_master.common.core.domain.character

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.Serializable

@Serializable
@Immutable
@Parcelize
data class SocialStatus(
    val tier: Tier,
    val standing: Int,
) : Parcelable {
    init {
        require(standing >= 0) { "Status standing must be non-negative" }
    }

    enum class Tier(
        override val translatableName: StringResource,
    ) : NamedEnum {
        BRASS(Str.social_status_brass),
        SILVER(Str.social_status_silver),
        GOLD(Str.social_status_gold),
    }
}
