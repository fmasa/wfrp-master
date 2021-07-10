package cz.frantisekmasa.wfrp_master.core.domain.character

import cz.frantisekmasa.wfrp_master.core.R
import cz.frantisekmasa.wfrp_master.core.domain.NamedEnum

data class SocialStatus(
    val tier: Tier,
    val standing: Int,
) {
    init {
        require(standing >= 0) { "Status standing must be non-negative" }
    }

    enum class Tier(override val nameRes: Int) : NamedEnum {
        BRASS(R.string.social_status_brass),
        SILVER(R.string.social_status_silver),
        GOLD(R.string.social_status_gold),
    }
}
