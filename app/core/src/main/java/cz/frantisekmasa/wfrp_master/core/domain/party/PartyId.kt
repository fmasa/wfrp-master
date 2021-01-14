package cz.frantisekmasa.wfrp_master.core.domain.party

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class PartyId private constructor(@JsonValue private val value: UUID) : Parcelable {
    companion object {
        fun fromString(id: String): PartyId = PartyId(UUID.fromString(id))
        fun generate(): PartyId = PartyId(UUID.randomUUID())
    }

    override fun toString() = value.toString()
}