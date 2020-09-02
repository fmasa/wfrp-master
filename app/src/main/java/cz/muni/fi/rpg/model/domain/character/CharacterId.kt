package cz.muni.fi.rpg.model.domain.character

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
class CharacterId(val partyId: UUID, val id: String) : Parcelable {
    companion object {
        fun forUser(partyId: UUID, userId: String) = CharacterId(partyId, userId)
    }

    fun isDerivedFromUserId(userId: String): Boolean = id == userId
}