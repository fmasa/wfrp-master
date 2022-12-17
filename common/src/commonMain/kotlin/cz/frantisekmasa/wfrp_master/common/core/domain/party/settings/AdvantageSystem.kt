package cz.frantisekmasa.wfrp_master.common.core.domain.party.settings

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings

enum class AdvantageSystem(
    override val nameResolver: (strings: Strings) -> String
) : NamedEnum {
    CORE_RULEBOOK({ it.combat.advantageSystems.coreRulebook }),
    GROUP_ADVANTAGE({ it.combat.advantageSystems.groupAdvantage }),
}
