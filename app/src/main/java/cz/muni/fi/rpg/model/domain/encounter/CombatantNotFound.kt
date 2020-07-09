package cz.muni.fi.rpg.model.domain.encounter

import java.lang.Exception

class CombatantNotFound(combatantId: CombatantId, cause: Throwable?) :
    Exception("Combatant $combatantId was not found", cause)