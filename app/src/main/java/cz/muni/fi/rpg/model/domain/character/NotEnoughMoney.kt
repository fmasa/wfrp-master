package cz.muni.fi.rpg.model.domain.character

import cz.muni.fi.rpg.model.domain.common.Money
import java.lang.Exception

class NotEnoughMoney(requiredAmount: Money, cause: Throwable)
    : Exception("Character does not have enough money to give $requiredAmount", cause)