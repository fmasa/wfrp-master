package cz.frantisekmasa.wfrp_master.core.domain.identifiers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class NpcId(val encounterId: EncounterId, val npcId: UUID) : Parcelable
