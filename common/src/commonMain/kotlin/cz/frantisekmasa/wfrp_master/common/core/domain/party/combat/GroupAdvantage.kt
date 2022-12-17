package cz.frantisekmasa.wfrp_master.common.core.domain.party.combat

import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GroupAdvantage(
    val allies: Advantage,
    val enemies: Advantage,
) : Parcelable
