package cz.frantisekmasa.wfrp_master.common.core.domain.party.combat

import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("@type")
sealed class Combatant : Parcelable {
    abstract val advantage: Int
    abstract val initiative: Int

    abstract fun withAdvantage(advantage: Int): Combatant
    abstract fun withInitiative(initiative: Int): Combatant

    fun areSameEntity(other: Combatant): Boolean {
        return (this is Character && other is Character && characterId == other.characterId) ||
            (this is Npc && other is Npc && npcId == other.npcId)
    }

    @Parcelize
    @Serializable
    @SerialName("character")
    data class Character(
        val characterId: String,
        override val initiative: Int,
        override val advantage: Int = 0
    ) : Combatant() {
        override fun withAdvantage(advantage: Int): Character = copy(advantage = advantage)
        override fun withInitiative(initiative: Int): Character = copy(initiative = initiative)
    }

    @Parcelize
    @Serializable
    @SerialName("npc")
    data class Npc(
        val npcId: NpcId,
        override val initiative: Int,
        override val advantage: Int = 0
    ) : Combatant() {
        override fun withAdvantage(advantage: Int): Npc = copy(advantage = advantage)
        override fun withInitiative(initiative: Int): Npc = copy(initiative = initiative)
    }
}
