package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.StringResource

@Parcelize
@Immutable
enum class Reach : NamedEnum, Parcelable {
    PERSONAL,
    VERY_SHORT,
    SHORT,
    AVERAGE,
    LONG,
    VERY_LONG,
    MASSIVE,
    ;

    override val translatableName: StringResource get() =
        when (this) {
            PERSONAL -> Str.weapons_reach_personal
            VERY_SHORT -> Str.weapons_reach_very_short
            SHORT -> Str.weapons_reach_short
            AVERAGE -> Str.weapons_reach_average
            LONG -> Str.weapons_reach_long
            VERY_LONG -> Str.weapons_reach_very_long
            MASSIVE -> Str.weapons_reach_massive
        }
}
