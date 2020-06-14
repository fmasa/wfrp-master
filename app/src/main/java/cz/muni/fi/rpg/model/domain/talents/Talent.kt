package cz.muni.fi.rpg.model.domain.talents

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Talent(
    val id: UUID,
    val name: String,
    val description: String,
    val taken: Int
) : Parcelable {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 200
    }

    init {
        require(name.isNotEmpty())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
        require(taken in 1..999) { "Skill can be taken 1-100x"}
    }
}
