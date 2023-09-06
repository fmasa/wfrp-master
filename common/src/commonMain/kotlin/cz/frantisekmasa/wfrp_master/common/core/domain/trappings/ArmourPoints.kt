package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Parcelize
@Serializable
@Immutable
value class ArmourPoints(val value: Int) : Parcelable {
    init {
        require(value >= 0) { "Armour points cannot be negative" }
    }
}
