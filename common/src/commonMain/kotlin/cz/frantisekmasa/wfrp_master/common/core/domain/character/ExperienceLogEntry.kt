package cz.frantisekmasa.wfrp_master.common.core.domain.character

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ExperienceLogEntry(
    @Contextual val id: Uuid,
    val amount: Int,
    val reason: String,
    val createdAt: Instant,
    val userId: UserId?,
)