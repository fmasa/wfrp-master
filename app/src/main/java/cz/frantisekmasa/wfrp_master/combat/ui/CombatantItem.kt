package cz.frantisekmasa.wfrp_master.combat.ui

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Wounds
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc as NpcEntity
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character as CharacterEntity

@Immutable
sealed class CombatantItem {
    abstract val combatant: Combatant
    abstract val name: String
    abstract val characteristics: Stats
    abstract val wounds: Wounds

    fun areSameEntity(other: CombatantItem): Boolean = combatant.areSameEntity(other.combatant)

    @Immutable
    data class Character(
        val characterId: CharacterId,
        private val character: CharacterEntity,
        override val combatant: Combatant.Character,
    ) : CombatantItem() {

        val userId: String?
            get() = character.userId

        val avatarUrl: String?
            get() = character.avatarUrl

        override val name
            get() = character.name

        override val characteristics
            get() = character.characteristics

        override val wounds
            get() = character.points.let { Wounds(current = it.wounds, max = it.maxWounds) }
    }

    @Immutable
    data class Npc(
        val npcId: NpcId,
        private val npc: NpcEntity,
        override val combatant: Combatant.Npc,
    ) : CombatantItem() {
        override val name
            get() = npc.name

        override val characteristics
            get() = npc.stats

        override val wounds
            get() = npc.wounds
    }
}
