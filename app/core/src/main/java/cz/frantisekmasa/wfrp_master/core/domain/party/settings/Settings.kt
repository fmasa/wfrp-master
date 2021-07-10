package cz.frantisekmasa.wfrp_master.core.domain.party.settings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Settings(
    val initiativeStrategy: InitiativeStrategy = InitiativeStrategy.INITIATIVE_CHARACTERISTIC,
) : Parcelable
