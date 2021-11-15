package cz.frantisekmasa.wfrp_master.common.core.domain

import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
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
