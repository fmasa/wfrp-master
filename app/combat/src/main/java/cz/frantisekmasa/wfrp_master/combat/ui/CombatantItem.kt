package cz.frantisekmasa.wfrp_master.combat.ui

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combatant

sealed class CombatantItem {
    abstract val combatant: Combatant
    abstract val name: String

    data class Character(
        val characterId: CharacterId,
        val userId: String?,
        override val name: String,
        override val combatant: Combatant.Character,
    ) : CombatantItem() {
    }

    data class Npc(
        val npcId: NpcId,
        override val name: String,
        override val combatant: Combatant.Npc,
    ) : CombatantItem()
}