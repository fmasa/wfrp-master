package cz.frantisekmasa.wfrp_master.common.core.domain.identifiers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
data class NpcId(
    val encounterId: EncounterId,
    @Contextual val npcId: UUID,
) : Parcelable
