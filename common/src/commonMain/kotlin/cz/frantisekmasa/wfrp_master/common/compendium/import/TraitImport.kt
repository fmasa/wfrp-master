package cz.frantisekmasa.wfrp_master.common.compendium.import

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class TraitImport(
    val name: String,
    val specifications: Set<String>,
    val description: String,
    val isVisibleToPlayers: Boolean = true,
) {
    init {
        require(name.isNotBlank()) { "Trait name cannot be blank" }
        name.requireMaxLength(Trait.NAME_MAX_LENGTH, "trait name")
        specifications.forEach {
            require(it in name) {
                "Trait name \"$name\" does not contain placeholder for specification \"$it\""
            }
        }
        require(specifications.all { name.contains(it) })
        description.requireMaxLength(Trait.DESCRIPTION_MAX_LENGTH, "trait description")
    }

    fun toTrait() = Trait(
        id = uuid4(),
        name = name,
        specifications = specifications,
        description = description,
        isVisibleToPlayers = isVisibleToPlayers,
    )

    companion object {
        fun fromTrait(trait: Trait) = TraitImport(
            name = trait.name,
            specifications = trait.specifications,
            description = trait.description,
            isVisibleToPlayers = trait.isVisibleToPlayers,
        )
    }
}
