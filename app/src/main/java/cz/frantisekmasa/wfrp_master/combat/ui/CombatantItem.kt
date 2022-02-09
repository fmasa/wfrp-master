package cz.frantisekmasa.wfrp_master.combat.ui

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Wounds
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc as NpcEntity
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character as CharacterEntity

sealed class CombatantItem {
    abstract val combatant: Combatant
    abstract val name: String
    abstract val characteristics: Stats
    abstract val wounds: Wounds

    fun areSameEntity(other: CombatantItem): Boolean = combatant.areSameEntity(other.combatant)

    data class Character(
        val characterId: CharacterId,
        private val character: CharacterEntity,
        override val combatant: Combatant.Character,
    ) : CombatantItem() {

        val userId: String?
            get() = character.userId

        val avatarUrl: String?
            get() = character.getAvatarUrl()

        override val name
            get() = character.getName()

        override val characteristics
            get() = character.getCharacteristics()

        override val wounds
            get() = character.getPoints().let {
                Wounds(
                    current = it.wounds,
                    max = it.maxWounds,
                )
            }
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
