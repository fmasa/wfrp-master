package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings
import kotlinx.serialization.Serializable

@Serializable
data class SocialStatus(
    val tier: Tier,
    val standing: Int,
) {
    init {
        require(standing >= 0) { "Status standing must be non-negative" }
    }

    enum class Tier(override val nameResolver: (strings: Strings) -> String) : NamedEnum {
        BRASS({ it.socialStatusBrass }),
        SILVER({ it.socialStatusSilver }),
        GOLD({ it.socialStatusGold }),
    }
}
