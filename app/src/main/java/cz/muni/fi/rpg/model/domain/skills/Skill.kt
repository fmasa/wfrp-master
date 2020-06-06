package cz.muni.fi.rpg.model.domain.skills

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
data class Skill(
    val id: UUID,
    val advanced: Boolean,
    val characteristic: SkillCharacteristic,
    val name: String,
    val description: String,
    val mastery: Int = 1
) : Parcelable {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 200
    }

    init {
        require(name.isNotEmpty())
        require(mastery in 1..3)
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
    }
}
