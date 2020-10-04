package cz.muni.fi.rpg.model.domain.encounter

import java.lang.Exception

class CombatantNotFound(npcId: NpcId, cause: Throwable?) :
    Exception("Combatant $npcId was not found", cause)