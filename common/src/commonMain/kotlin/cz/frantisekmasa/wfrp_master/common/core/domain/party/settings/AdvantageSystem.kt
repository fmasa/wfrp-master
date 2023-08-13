package cz.frantisekmasa.wfrp_master.common.core.domain.party.settings

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

enum class AdvantageSystem(
    override val translatableName: StringResource
) : NamedEnum {
    CORE_RULEBOOK(Str.combat_advantage_systems_core_rulebook),
    GROUP_ADVANTAGE(Str.combat_advantage_systems_group_advantage),
}
