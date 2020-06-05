package cz.muni.fi.rpg.model.domain.character

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
class CharacterId(val partyId: UUID, val userId: String) : Parcelable