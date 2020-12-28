package cz.frantisekmasa.wfrp_master.core.domain.identifiers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import java.util.*

@Parcelize
data class EncounterId(
    @TypeParceler<UUID, UUIDParceler>
    val partyId: UUID,

    @TypeParceler<UUID, UUIDParceler>
    val encounterId: UUID
) : Parcelable