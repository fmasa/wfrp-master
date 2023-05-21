package cz.frantisekmasa.wfrp_master.common.compendium.import

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class SpellImport(
    val name: String,
    val range: String,
    val target: String,
    val duration: String,
    val castingNumber: Int,
    val effect: String,
    val lore: String,
    val isVisibleToPlayers: Boolean = true,
) {
    init {
        require(name.isNotBlank()) { "Spell name cannot be blank" }
        name.requireMaxLength(Spell.NAME_MAX_LENGTH, "spell name")
        require(castingNumber >= 0) { "Casting number cannot be negative for spell \"$name\"" }
        range.requireMaxLength(Spell.RANGE_MAX_LENGTH, "spell range")
        target.requireMaxLength(Spell.TARGET_MAX_LENGTH, "spell target")
        duration.requireMaxLength(Spell.DURATION_MAX_LENGTH, "spell duration")
        effect.requireMaxLength(Spell.EFFECT_MAX_LENGTH, "spell effect")
        lore.requireMaxLength(Spell.LORE_MAX_LENGTH, "spell lore")
    }

    fun toSpell() = Spell(
        id = uuid4(),
        name = name,
        range = range,
        target = target,
        duration = duration,
        castingNumber = castingNumber,
        effect = effect,
        lore = lore,
        isVisibleToPlayers = isVisibleToPlayers,
    )

    companion object {
        fun fromSpell(spell: Spell) = SpellImport(
            name = spell.name,
            range = spell.range,
            target = spell.target,
            duration = spell.duration,
            castingNumber = spell.castingNumber,
            effect = spell.effect,
            lore = spell.lore,
            isVisibleToPlayers = spell.isVisibleToPlayers,
        )
    }
}
