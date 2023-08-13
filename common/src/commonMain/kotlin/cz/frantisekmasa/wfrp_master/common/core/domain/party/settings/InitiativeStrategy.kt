package cz.frantisekmasa.wfrp_master.common.core.domain.party.settings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.resources.StringResource

@Immutable
enum class InitiativeStrategy(
    override val translatableName: StringResource,
) : NamedEnum {
    INITIATIVE_CHARACTERISTIC(Str.combat_initiative_strategies_initiative_characteristic),
    INITIATIVE_TEST(Str.combat_initiative_strategies_initiative_test),
    INITIATIVE_PLUS_1D10(Str.combat_initiative_strategies_initiative_plus_1d10),
    BONUSES_PLUS_1D10(Str.combat_initiative_strategies_bonuses_plus_1d10),
}
