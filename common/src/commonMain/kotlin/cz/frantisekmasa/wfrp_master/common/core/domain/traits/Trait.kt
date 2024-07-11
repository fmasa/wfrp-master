package cz.frantisekmasa.wfrp_master.common.core.domain.traits

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.effects.CharacterEffect
import cz.frantisekmasa.wfrp_master.common.character.effects.CharacteristicChange
import cz.frantisekmasa.wfrp_master.common.character.effects.ConstructWoundsModification
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectSource
import cz.frantisekmasa.wfrp_master.common.character.effects.SizeChange
import cz.frantisekmasa.wfrp_master.common.character.effects.SwarmWoundsModification
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import cz.frantisekmasa.wfrp_master.common.localization.Translator
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait as CompendiumTrait

@Parcelize
@Serializable
@Immutable
data class Trait(
    override val id: UuidAsString,
    override val compendiumId: UuidAsString,
    val name: String,
    val specificationValues: Map<String, String>,
    val description: String,
) : CharacterItem<Trait, CompendiumTrait>, EffectSource {
    @Stable
    val evaluatedName get(): String =
        specificationValues
            .toList()
            .fold(name) { name, (search, replacement) ->
                val lastIndex = name.lastIndexOf(search)

                if (lastIndex != -1) {
                    name.replaceRange(lastIndex, lastIndex + search.length, replacement)
                } else {
                    name
                }
            }

    @Stable
    override fun getEffects(translator: Translator): List<CharacterEffect> {
        val name = evaluatedName.trim()

        return listOfNotNull(
            SizeChange.fromTraitNameOrNull(name, translator),
            CharacteristicChange.fromTraitNameOrNull(name, translator),
            SwarmWoundsModification.fromTraitNameOrNull(name, translator),
            ConstructWoundsModification.fromTraitNameOrNull(name, translator),
        )
    }

    override fun updateFromCompendium(compendiumItem: CompendiumTrait): Trait {
        if (compendiumItem.specifications != specificationValues.keys) {
            return this // TODO: Unlink from compendium item
        }

        return copy(
            name = compendiumItem.name,
            description = compendiumItem.description,
        )
    }

    override fun unlinkFromCompendium() = this // TODO: Unlink from compendium item

    init {
        require(specificationValues.keys.all { name.contains(it) })
        require(name.isNotEmpty())
        require(name.length <= CompendiumTrait.NAME_MAX_LENGTH) {
            "Maximum allowed name length is ${CompendiumTrait.NAME_MAX_LENGTH}"
        }
        require(description.length <= CompendiumTrait.DESCRIPTION_MAX_LENGTH) {
            "Maximum allowed description length is ${CompendiumTrait.DESCRIPTION_MAX_LENGTH}"
        }
    }

    companion object {
        fun fromCompendium(
            compendiumTrait: CompendiumTrait,
            specificationValues: Map<String, String>,
        ): Trait {
            return Trait(
                id = uuid4(),
                compendiumId = compendiumTrait.id,
                name = compendiumTrait.name,
                description = compendiumTrait.description,
                specificationValues = specificationValues.toMap(),
            )
        }
    }
}
