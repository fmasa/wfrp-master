package cz.frantisekmasa.wfrp_master.common.core.domain.party.settings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings

@Immutable
enum class InitiativeStrategy(
    override val nameResolver: (strings: Strings) -> String
) : NamedEnum {
    INITIATIVE_CHARACTERISTIC({ it.combat.initiativeStrategies.initiativeCharacteristic }),
    INITIATIVE_TEST({ it.combat.initiativeStrategies.initiativeTest }),
    INITIATIVE_PLUS_1D10({ it.combat.initiativeStrategies.initiativePlus1d10 }),
    BONUSES_PLUS_1D10({ it.combat.initiativeStrategies.bonusesPlus1d10 }),
}
