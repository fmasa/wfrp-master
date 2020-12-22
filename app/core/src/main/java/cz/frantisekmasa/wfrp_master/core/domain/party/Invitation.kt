package cz.frantisekmasa.wfrp_master.core.domain.party

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Invitation(
    val partyId: UUID,
    val partyName: String,
    val accessCode: String
) : Parcelable