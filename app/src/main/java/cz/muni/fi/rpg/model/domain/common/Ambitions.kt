package cz.muni.fi.rpg.model.domain.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ambitions(
    val shortTerm: String,
    val longTerm: String
): Parcelable {
    companion object {
        const val MAX_LENGTH = 400
    }

    init {
        require(shortTerm.length <= MAX_LENGTH)
        require(longTerm.length <= MAX_LENGTH)
    }
}