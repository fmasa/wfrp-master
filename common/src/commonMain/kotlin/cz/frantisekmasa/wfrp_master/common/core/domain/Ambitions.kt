package cz.frantisekmasa.wfrp_master.common.core.domain

import androidx.compose.runtime.Immutable
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Ambitions(
    val shortTerm: String,
    val longTerm: String
) : Parcelable {
    companion object {
        const val MAX_LENGTH = 400
    }

    init {
        require(shortTerm.length <= MAX_LENGTH)
        require(longTerm.length <= MAX_LENGTH)
    }
}
