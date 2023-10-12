package cz.frantisekmasa.wfrp_master.common.core.domain.talents

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.character.effects.AdditionalEncumbrance
import cz.frantisekmasa.wfrp_master.common.character.effects.CharacterEffect
import cz.frantisekmasa.wfrp_master.common.character.effects.CharacteristicChange
import cz.frantisekmasa.wfrp_master.common.character.effects.EffectSource
import cz.frantisekmasa.wfrp_master.common.character.effects.HardyWoundsModification
import cz.frantisekmasa.wfrp_master.common.character.effects.Translator
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent as CompendiumTalent

@Parcelize
@Serializable
@Immutable
data class Talent(
    @Contextual override val id: Uuid,
    @Contextual override val compendiumId: Uuid? = null,
    val name: String,
    val tests: String = "", // TODO: Remove default value in 3.0
    val description: String,
    val taken: Int
) : CharacterItem<Talent, CompendiumTalent>, EffectSource {

    @Stable
    override fun getEffects(translator: Translator): List<CharacterEffect> {
        val name = name.trim()

        return listOfNotNull(
            HardyWoundsModification.fromTalentOrNull(name, translator, taken),
            CharacteristicChange.fromTalentNameOrNull(name, translator),
            AdditionalEncumbrance.fromTalentOrNull(name, translator, taken),
        )
    }

    override fun updateFromCompendium(compendiumItem: CompendiumTalent): Talent {
        return copy(
            name = compendiumItem.name,
            tests = compendiumItem.tests,
            description = compendiumItem.description,
        )
    }

    override fun unlinkFromCompendium() = copy(compendiumId = null)

    init {
        require(name.isNotEmpty())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
        tests.requireMaxLength(CompendiumTalent.TESTS_MAX_LENGTH, "tests")
        require(taken in 1..999) { "Skill can be taken 1-100x" }
    }

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 1500

        fun fromCompendium(compendiumTalent: CompendiumTalent, timesTaken: Int): Talent {
            return Talent(
                id = uuid4(),
                compendiumId = compendiumTalent.id,
                name = compendiumTalent.name,
                tests = compendiumTalent.tests,
                description = compendiumTalent.description,
                taken = timesTaken,
            )
        }
    }
}
