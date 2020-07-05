package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.TypeParceler
import java.util.*

@Parcelize
data class EncounterId(
    @TypeParceler<UUID, UUIDParceler>
    val partyId: UUID,

    @TypeParceler<UUID, UUIDParceler>
    val encounterId: UUID
) : Parcelable