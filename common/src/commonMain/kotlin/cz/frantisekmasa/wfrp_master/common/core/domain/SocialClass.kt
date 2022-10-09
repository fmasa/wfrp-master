package cz.frantisekmasa.wfrp_master.common.core.domain

import cz.frantisekmasa.wfrp_master.common.localization.Strings

enum class SocialClass(
    override val nameResolver: (strings: Strings) -> String
) : NamedEnum {
    ACADEMICS({ it.socialClasses.academics }),
    BURGHERS({ it.socialClasses.burghers }),
    COURTIERS({ it.socialClasses.courtiers }),
    PEASANTS({ it.socialClasses.peasants }),
    RANGERS({ it.socialClasses.rangers }),
    RIVERFOLK({ it.socialClasses.riverfolk }),
    ROGUES({ it.socialClasses.rogues }),
    WARRIORS({ it.socialClasses.warriors }),
}
