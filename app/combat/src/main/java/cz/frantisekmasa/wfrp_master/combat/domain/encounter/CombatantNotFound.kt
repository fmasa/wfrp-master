package cz.frantisekmasa.wfrp_master.combat.domain.encounter

import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import java.lang.Exception

class CombatantNotFound(npcId: NpcId, cause: Throwable?) :
    Exception("Combatant $npcId was not found", cause)
