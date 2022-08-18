package cz.frantisekmasa.wfrp_master.common.core.domain.party.combat

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@Immutable
@JsonClassDiscriminator("@type")
sealed class Combatant : Parcelable {
    abstract val id: Uuid?
    abstract val advantage: Advantage
    abstract val initiative: Int
    abstract val name: String?
    abstract val wounds: Wounds?

    abstract fun withAdvantage(advantage: Advantage): Combatant
    abstract fun withInitiative(initiative: Int): Combatant
    abstract fun withWounds(wounds: Wounds): Combatant

    fun areSameEntity(other: Combatant): Boolean {
        if (id != null || other.id != null) {
            return id == other.id
        }

        return (this is Character && other is Character && characterId == other.characterId) ||
            (this is Npc && other is Npc && npcId == other.npcId)
    }

    @Parcelize
    @Serializable
    @SerialName("character")
    @Immutable
    data class Character(
        val characterId: String,
        @Contextual override val id: Uuid? = null,
        override val initiative: Int,
        override val name: String? = null,
        override val wounds: Wounds? = null,
        override val advantage: Advantage = Advantage.ZERO,
    ) : Combatant() {
        override fun withAdvantage(advantage: Advantage): Character = copy(advantage = advantage)
        override fun withInitiative(initiative: Int): Character = copy(initiative = initiative)
        override fun withWounds(wounds: Wounds) = copy(wounds = wounds)
    }

    @Parcelize
    @Serializable
    @SerialName("npc")
    @Immutable
    data class Npc(
        val npcId: NpcId,
        @Contextual override val id: Uuid? = null,
        override val initiative: Int,
        override val name: String? = null,
        override val wounds: Wounds? = null,
        override val advantage: Advantage = Advantage.ZERO
    ) : Combatant() {
        override fun withAdvantage(advantage: Advantage): Npc = copy(advantage = advantage)
        override fun withInitiative(initiative: Int): Npc = copy(initiative = initiative)
        override fun withWounds(wounds: Wounds) = copy(wounds = wounds)
    }
}
