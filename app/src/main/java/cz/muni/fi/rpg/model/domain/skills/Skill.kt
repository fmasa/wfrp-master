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
    val description: String
): Parcelable {
    init {
        require(name.isNotEmpty())
    }
}
