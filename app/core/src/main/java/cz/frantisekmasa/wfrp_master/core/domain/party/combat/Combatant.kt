package cz.frantisekmasa.wfrp_master.core.domain.party.combat

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import kotlinx.parcelize.Parcelize

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
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
    @JsonTypeName("character")
    data class Character(
        val characterId: String,
        override val initiative: Int,
        override val advantage: Int = 0
    ) : Combatant() {
        override fun withAdvantage(advantage: Int): Character = copy(advantage = advantage)
        override fun withInitiative(initiative: Int): Character = copy(initiative = initiative)
    }

    @Parcelize
    @JsonTypeName("npc")
    data class Npc(
        val npcId: NpcId,
        override val initiative: Int,
        override val advantage: Int = 0
    ) : Combatant() {
        override fun withAdvantage(advantage: Int): Npc = copy(advantage = advantage)
        override fun withInitiative(initiative: Int): Npc = copy(initiative = initiative)
    }
}
