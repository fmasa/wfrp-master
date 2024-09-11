package cz.frantisekmasa.wfrp_master.common.core.domain.character

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.Plurals
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.PluralsResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Countdown(
    val value: Int,
    val unit: Unit,
) : Parcelable {
    init {
        require(value >= 0) { "Remaining time must be non-negative" }
    }

    enum class Unit(
        override val translatableName: StringResource,
        val plural: PluralsResource,
    ) : NamedEnum {
        DAYS(Str.common_ui_units_days, Plurals.duration_days),
        HOURS(Str.common_ui_units_hours, Plurals.duration_hours),
        MINUTES(Str.common_ui_units_minutes, Plurals.duration_minutes),
    }

    @Composable
    @Stable
    fun toText(): String = stringResource(unit.plural, value, value)
}
