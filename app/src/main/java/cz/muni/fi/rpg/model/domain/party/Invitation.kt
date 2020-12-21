package cz.muni.fi.rpg.model.domain.party

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Invitation(
    val partyId: UUID,
    val partyName: String,
    val accessCode: String
) : Parcelable