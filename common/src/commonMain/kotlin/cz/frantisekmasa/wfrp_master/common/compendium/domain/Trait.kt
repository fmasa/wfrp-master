package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Trait(
    @Contextual override val id: Uuid,
    override val name: String,
    val specifications: Set<String>,
    val description: String,
) : CompendiumItem<Trait>() {
    init {
        require(specifications.all { name.contains(it) })
        require(name.isNotBlank())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
    }

    override fun replace(original: Trait) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 3000
    }
}
