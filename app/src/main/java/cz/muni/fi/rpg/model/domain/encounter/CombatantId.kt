package cz.muni.fi.rpg.model.domain.encounter

import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import java.util.*

data class CombatantId(val encounterId: EncounterId, val combatantId: UUID)