package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

enum class CharacterTab(
    override val translatableName: StringResource,
) : NamedEnum {
    ATTRIBUTES(Str.character_tab_attributes),
    COMBAT(Str.character_tab_combat),
    CONDITIONS(Str.character_tab_conditions),
    SKILLS_AND_TALENTS(Str.character_tab_skills_and_talents),
    SPELLS(Str.character_tab_spells),
    RELIGION(Str.character_tab_religions),
    TRAPPINGS(Str.character_tab_trappings),
    WELL_BEING(Str.character_title_well_being),
    NOTES(Str.character_tab_notes),
}
