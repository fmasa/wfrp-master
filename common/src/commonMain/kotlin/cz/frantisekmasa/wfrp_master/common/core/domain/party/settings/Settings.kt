package cz.frantisekmasa.wfrp_master.common.core.domain.party.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Settings(
    val initiativeStrategy: InitiativeStrategy = InitiativeStrategy.INITIATIVE_CHARACTERISTIC,
) : Parcelable
