package cz.frantisekmasa.wfrp_master.common.core.domain.identifiers

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
@Immutable
data class NpcId(
    val encounterId: EncounterId,
    @Contextual val npcId: UUID,
) : Parcelable
