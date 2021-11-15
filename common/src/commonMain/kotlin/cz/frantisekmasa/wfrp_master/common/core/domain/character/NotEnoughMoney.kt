package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import java.lang.Exception

class NotEnoughMoney(requiredAmount: Money, cause: Throwable) :
    Exception("Character does not have enough money to give $requiredAmount", cause)
