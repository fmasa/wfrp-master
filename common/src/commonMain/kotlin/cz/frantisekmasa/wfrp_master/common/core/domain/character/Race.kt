package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.Size
import dev.icerock.moko.resources.StringResource

enum class Race(
    override val translatableName: StringResource,
) : NamedEnum {
    HUMAN(Str.races_human),
    HIGH_ELF(Str.races_high_elf),
    DWARF(Str.races_dwarf),
    WOOD_ELF(Str.races_wood_elf),
    HALFLING(Str.races_halfling),
    GNOME(Str.races_gnome),
    OGRE(Str.races_ogre),
    ;

    val size: Size get() =
        when (this) {
            HALFLING, GNOME -> Size.SMALL
            OGRE -> Size.LARGE
            else -> Size.AVERAGE
        }
}
