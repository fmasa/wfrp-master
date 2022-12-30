package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Blessing(
    @Contextual override val id: Uuid,
    override val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
) : CompendiumItem<Blessing>() {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val RANGE_MAX_LENGTH = 50
        const val TARGET_MAX_LENGTH = 50
        const val DURATION_MAX_LENGTH = 50
        const val EFFECT_MAX_LENGTH = 1000
    }
    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        range.requireMaxLength(RANGE_MAX_LENGTH, "range")
        target.requireMaxLength(TARGET_MAX_LENGTH, "target")
        duration.requireMaxLength(DURATION_MAX_LENGTH, "duration")
        effect.requireMaxLength(EFFECT_MAX_LENGTH, "effect")
    }

    override fun replace(original: Blessing) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())
}
