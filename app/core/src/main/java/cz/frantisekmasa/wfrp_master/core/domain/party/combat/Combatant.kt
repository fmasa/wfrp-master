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

    @Parcelize
    @JsonTypeName("character")
    data class Character(
        val characterId: String,
        override val initiative: Int,
        override val advantage: Int = 0
    ) : Combatant() {
        override fun withAdvantage(advantage: Int): Character = copy(advantage = advantage)
        override fun withInitiative(initiative: Int): Character = copy(initiative = initiative)

        override fun equals(other: Any?): Boolean {
            return other is Character && other.characterId == characterId
        }
        override fun hashCode() = characterId.hashCode()

        override fun toString() = "Combatant.Character (characterId = $characterId)"
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

        override fun equals(other: Any?): Boolean {
            return other is Npc && other.npcId == npcId
        }
        override fun hashCode() = npcId.hashCode()

        override fun toString() = "Combatant.Npc (npcId = ${npcId.npcId})"
    }
}