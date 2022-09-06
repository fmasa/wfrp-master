package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.Size
import cz.frantisekmasa.wfrp_master.common.localization.Strings

enum class Race(override val nameResolver: (strings: Strings) -> String) : NamedEnum {
    HUMAN({ it.races.human }),
    HIGH_ELF({ it.races.highElf }),
    DWARF({ it.races.dwarf }),
    WOOD_ELF({ it.races.woodElf }),
    HALFLING({ it.races.halfling }),
    GNOME({ it.races.gnome }),
    OGRE({ it.races.ogre });

    val size: Size get() = when (this) {
        HALFLING, GNOME -> Size.SMALL
        OGRE -> Size.LARGE
        else -> Size.AVERAGE
    }
}
