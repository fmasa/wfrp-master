package cz.frantisekmasa.wfrp_master.core.domain.identifiers

import android.os.Parcel
import kotlinx.parcelize.Parceler
import java.util.*

internal object UUIDParceler : Parceler<UUID> {
    override fun create(parcel: Parcel): UUID {
        val mostSigBits = parcel.readLong()
        val leastSigBits = parcel.readLong()
        return UUID(mostSigBits, leastSigBits)
    }

    override fun UUID.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(mostSignificantBits)
        parcel.writeLong(leastSignificantBits)
    }
}