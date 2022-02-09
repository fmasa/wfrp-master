package cz.frantisekmasa.wfrp_master.common.core.domain.party.settings

import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Settings(
    val initiativeStrategy: InitiativeStrategy = InitiativeStrategy.INITIATIVE_CHARACTERISTIC,
) : Parcelable
