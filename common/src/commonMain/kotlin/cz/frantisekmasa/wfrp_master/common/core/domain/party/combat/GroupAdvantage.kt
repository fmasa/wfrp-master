package cz.frantisekmasa.wfrp_master.common.core.domain.party.combat

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class GroupAdvantage(
    val allies: Advantage,
    val enemies: Advantage,
) : Parcelable
