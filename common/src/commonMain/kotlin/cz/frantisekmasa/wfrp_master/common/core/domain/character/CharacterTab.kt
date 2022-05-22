package cz.frantisekmasa.wfrp_master.common.core.domain.character

import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.localization.Strings

enum class CharacterTab(override val nameResolver: (strings: Strings) -> String): NamedEnum {
    ATTRIBUTES({ it.character.tabAttributes }),
    COMBAT({ it.character.tabCombat }),
    CONDITIONS({ it.character.tabConditions }),
    SKILLS_AND_TALENTS({ it.character.tabSkillsAndTalents }),
    SPELLS({ it.character.tabSpells }),
    RELIGION({ it.character.tabReligions }),
    TRAPPINGS({ it.character.tabTrappings }),
}