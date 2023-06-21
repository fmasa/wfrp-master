package cz.frantisekmasa.wfrp_master.common.compendium.domain

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings

enum class SpellLore(
    override val nameResolver: (strings: Strings) -> String,
    val wind: String?,
) : NamedEnum {
    BEASTS({ it.spells.lores.beasts }, "Ghur"),
    DEATH({ it.spells.lores.death }, "Shyish"),
    FIRE({ it.spells.lores.fire }, "Aqshy"),
    HEAVENS({ it.spells.lores.heavens }, "Heavens"),
    METAL({ it.spells.lores.metal }, "Azyr"),
    LIFE({ it.spells.lores.life }, "Chamon"),
    LIGHT({ it.spells.lores.light }, "Ghyran"),
    SHADOWS({ it.spells.lores.shadows }, "Ulgu"),
    HEDGECRAFT({ it.spells.lores.hedgecraft }, null),
    WITCHCRAFT({ it.spells.lores.witchcraft }, null),
    DAEMONOLOGY({ it.spells.lores.daemonology }, "Dhar"),
    NECROMANCY({ it.spells.lores.necromancy }, "Dhar"),
    NURGLE({ it.spells.lores.nurgle }, "Dhar"),
    SLAANESH({ it.spells.lores.slaanesh }, "Dhar"),
    TZEENTCH({ it.spells.lores.tzeentch }, "Dhar"),
    PETTY({ it.spells.lores.petty }, null),
}
