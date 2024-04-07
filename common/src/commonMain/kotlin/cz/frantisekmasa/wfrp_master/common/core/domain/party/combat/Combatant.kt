package cz.frantisekmasa.wfrp_master.common.core.domain.party.combat

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CurrentConditions
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Immutable
@Parcelize
data class Combatant(
    val characterId: String,
    val id: UuidAsString? = null,
    val initiative: Int,
    val name: String? = null,
    val wounds: Wounds? = null,
    val advantage: Advantage = Advantage.ZERO,
    val conditions: CurrentConditions? = null,
) : Parcelable {

    fun withAdvantage(advantage: Advantage) = copy(advantage = advantage)
    fun withInitiative(initiative: Int) = copy(initiative = initiative)
    fun withWounds(wounds: Wounds) = copy(wounds = wounds)
    fun withConditions(conditions: CurrentConditions) = copy(conditions = conditions)

    fun areSameEntity(other: Combatant): Boolean {
        if (id != null || other.id != null) {
            return id == other.id
        }

        return characterId == other.characterId
    }
}
