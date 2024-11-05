package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Miracle(
    override val id: UuidAsString,
    override val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val effect: String,
    val cultName: String,
    override val isVisibleToPlayers: Boolean = true,
) : CompendiumItem<Miracle>() {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val RANGE_MAX_LENGTH = 50
        const val TARGET_MAX_LENGTH = 200
        const val DURATION_MAX_LENGTH = 50
        const val EFFECT_MAX_LENGTH = 1000
        const val CULT_NAME_MAX_LENGTH = 50
    }

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        range.requireMaxLength(RANGE_MAX_LENGTH, "range")
        target.requireMaxLength(TARGET_MAX_LENGTH, "target")
        duration.requireMaxLength(DURATION_MAX_LENGTH, "duration")
        effect.requireMaxLength(EFFECT_MAX_LENGTH, "effect")
        cultName.requireMaxLength(CULT_NAME_MAX_LENGTH, "cultName")
    }

    override fun replace(original: Miracle) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName(name, NAME_MAX_LENGTH))

    override fun changeVisibility(isVisibleToPlayers: Boolean) = copy(isVisibleToPlayers = isVisibleToPlayers)
}
