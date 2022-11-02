package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Skill(
    @Contextual override val id: Uuid,
    override val name: String,
    val description: String,
    val characteristic: Characteristic,
    val advanced: Boolean,
) : CompendiumItem<Skill>() {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 2000
    }

    init {
        require(name.isNotEmpty())
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        description.requireMaxLength(DESCRIPTION_MAX_LENGTH, "description")
    }

    override fun replace(original: Skill) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())
}
