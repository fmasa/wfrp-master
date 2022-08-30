package cz.frantisekmasa.wfrp_master.common.core.domain.traits

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Trait(
    @Contextual override val id: Uuid,
    @Contextual override val compendiumId: Uuid,
    val name: String,
    val specificationValues: Map<String, String>,
    val description: String,
) : CharacterItem {

    @Stable
    val evaluatedName get(): String = specificationValues
        .toList()
        .fold(name) { name, (search, replacement) -> name.replace(search, replacement) }

    init {
        if (description.length > Trait.DESCRIPTION_MAX_LENGTH) {
            println(description)
        }
        require(specificationValues.keys.all { name.contains(it) })
        require(name.isNotEmpty())
        require(name.length <= Trait.NAME_MAX_LENGTH) { "Maximum allowed name length is ${Trait.NAME_MAX_LENGTH}" }
        require(description.length <= Trait.DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is ${Trait.DESCRIPTION_MAX_LENGTH}" }
    }
}
