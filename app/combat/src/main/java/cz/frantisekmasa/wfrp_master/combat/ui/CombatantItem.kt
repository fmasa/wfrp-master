package cz.frantisekmasa.wfrp_master.combat.ui

import cz.frantisekmasa.wfrp_master.core.domain.party.combat.Combatant
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc as NpcEntity
import cz.frantisekmasa.wfrp_master.core.domain.character.Character as CharacterEntity

sealed class CombatantItem {
    abstract val combatant: Combatant

    data class Character(
        val character: CharacterEntity,
        override val combatant: Combatant.Character,
    ) : CombatantItem()

    data class Npc(
        val npc: NpcEntity,
        override val combatant: Combatant.Npc,
    ) : CombatantItem()
}