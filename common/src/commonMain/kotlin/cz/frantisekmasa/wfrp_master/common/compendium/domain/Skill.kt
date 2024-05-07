package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Skill(
    override val id: UuidAsString,
    override val name: String,
    val description: String,
    val characteristic: Characteristic,
    val advanced: Boolean,
    override val isVisibleToPlayers: Boolean = true,
) : CompendiumItem<Skill>() {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 2500
    }

    init {
        require(name.isNotEmpty())
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        description.requireMaxLength(DESCRIPTION_MAX_LENGTH, "description")
    }

    override fun replace(original: Skill) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())

    override fun changeVisibility(isVisibleToPlayers: Boolean) = copy(isVisibleToPlayers = isVisibleToPlayers)
}
