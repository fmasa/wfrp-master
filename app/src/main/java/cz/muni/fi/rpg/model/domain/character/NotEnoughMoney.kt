package cz.muni.fi.rpg.model.domain.character

import cz.frantisekmasa.wfrp_master.core.domain.Money
import java.lang.Exception

class NotEnoughMoney(requiredAmount: Money, cause: Throwable)
    : Exception("Character does not have enough money to give $requiredAmount", cause)