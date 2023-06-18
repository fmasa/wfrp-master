package cz.frantisekmasa.wfrp_master.common.core.domain.spells

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell as CompendiumSpell

@Parcelize
@Serializable
@Immutable
data class Spell(
    @Contextual override val id: Uuid,
    @Contextual override val compendiumId: Uuid? = null,
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val castingNumber: Int,
    val effect: String,
    val lore: SpellLore? = null,
    val memorized: Boolean = true, // TODO: Remove default value and migrate stored data
) : CharacterItem<Spell, CompendiumSpell> {

    val effectiveCastingNumber: Int get() = if (memorized)
        castingNumber
    else castingNumber * 2

    init {
        require(name.isNotBlank()) { "Name must not be blank" }
        require(castingNumber >= 0) { "Casting number cannot be negative" }
        require(name.length <= NAME_MAX_LENGTH) { "Name must be shorter than $NAME_MAX_LENGTH" }
        require(range.length <= RANGE_MAX_LENGTH) { "Range must be shorter than $RANGE_MAX_LENGTH" }
        require(target.length <= TARGET_MAX_LENGTH) { "Target must be shorter than $TARGET_MAX_LENGTH" }
        require(duration.length <= DURATION_MAX_LENGTH) { "Duration must be shorter than $DURATION_MAX_LENGTH" }
        require(effect.length <= EFFECT_MAX_LENGTH) { "Effect must be shorter than $EFFECT_MAX_LENGTH" }
    }

    override fun updateFromCompendium(compendiumItem: CompendiumSpell): Spell {
        return copy(
            compendiumId = compendiumItem.id,
            name = compendiumItem.name,
            range = compendiumItem.range,
            target = compendiumItem.target,
            duration = compendiumItem.duration,
            castingNumber = compendiumItem.castingNumber,
            effect = compendiumItem.effect,
        )
    }

    override fun unlinkFromCompendium() = copy(compendiumId = null)

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val RANGE_MAX_LENGTH = 50
        const val TARGET_MAX_LENGTH = 50
        const val DURATION_MAX_LENGTH = 50
        const val EFFECT_MAX_LENGTH = 1500

        fun fromCompendium(spell: CompendiumSpell): Spell {
            return Spell(
                id = uuid4(),
                compendiumId = spell.id,
                name = spell.name,
                range = spell.range,
                target = spell.target,
                duration = spell.duration,
                castingNumber = spell.castingNumber,
                effect = spell.effect,
                lore = spell.lore,
                memorized = false,
            )
        }
    }
}
