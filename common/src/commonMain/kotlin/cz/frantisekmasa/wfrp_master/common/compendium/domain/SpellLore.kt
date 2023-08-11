package cz.frantisekmasa.wfrp_master.common.compendium.domain

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

enum class SpellLore(
    override val translatableName: StringResource,
    val wind: String?,
) : NamedEnum {
    BEASTS(Str.spells_lores_beasts, "Ghur"),
    DEATH(Str.spells_lores_death, "Shyish"),
    FIRE(Str.spells_lores_fire, "Aqshy"),
    HEAVENS(Str.spells_lores_heavens, "Azyr"),
    METAL(Str.spells_lores_metal, "Chamon"),
    LIFE(Str.spells_lores_life, "Ghyran"),
    LIGHT(Str.spells_lores_light, "Hysh"),
    SHADOWS(Str.spells_lores_shadows, "Ulgu"),
    HEDGECRAFT(Str.spells_lores_hedgecraft, null),
    WITCHCRAFT(Str.spells_lores_witchcraft, null),
    DAEMONOLOGY(Str.spells_lores_daemonology, "Dhar"),
    NECROMANCY(Str.spells_lores_necromancy, "Dhar"),
    NURGLE(Str.spells_lores_nurgle, "Dhar"),
    SLAANESH(Str.spells_lores_slaanesh, "Dhar"),
    TZEENTCH(Str.spells_lores_tzeentch, "Dhar"),
    PETTY(Str.spells_lores_petty, null),
}
