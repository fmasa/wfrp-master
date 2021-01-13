package cz.frantisekmasa.wfrp_master.combat.domain.initiative

import cz.frantisekmasa.wfrp_master.core.domain.Stats

internal interface InitiativeStrategy {
    fun determineInitiative(characteristics: Stats): InitiativeOrder
}