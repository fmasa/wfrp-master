package cz.muni.fi.rpg.model.domain.character

import cz.muni.fi.rpg.R

enum class Race {
    HUMAN,
    ELF,
    DWARF,
    HALFLING,
    GNOME;

    fun getReadableNameId(): Int {
        return when (this) {
            HUMAN -> R.string.race_human
            ELF -> R.string.race_elf
            DWARF -> R.string.race_dwarf
            HALFLING -> R.string.race_halfling
            GNOME -> R.string.race_gnome
        }
    }
}