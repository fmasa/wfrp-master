package cz.frantisekmasa.wfrp_master.core.domain.identifiers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class CharacterId(val partyId: UUID, val id: String) : Parcelable {
    fun isDerivedFromUserId(userId: String): Boolean = id == userId
}