package cz.muni.fi.rpg.model.domain.encounter

import android.os.Parcelable
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class CombatantId(val encounterId: EncounterId, val combatantId: UUID) : Parcelable