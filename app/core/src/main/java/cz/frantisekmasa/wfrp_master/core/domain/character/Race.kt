package cz.frantisekmasa.wfrp_master.core.domain.character

import cz.frantisekmasa.wfrp_master.core.R

enum class Race {
    HUMAN,
    HIGH_ELF,
    DWARF,
    WOOD_ELF,
    HALFLING,
    GNOME;

    fun getReadableNameId(): Int {
        return when (this) {
            HUMAN -> R.string.race_human
            HIGH_ELF -> R.string.race_high_elf
            DWARF -> R.string.race_dwarf
            HALFLING -> R.string.race_halfling
            WOOD_ELF -> R.string.race_wood_elf
            GNOME -> R.string.race_gnome
        }
    }
}