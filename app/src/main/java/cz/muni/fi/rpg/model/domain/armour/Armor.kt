package cz.muni.fi.rpg.model.domain.armour

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Armor(
    val head: Int = 0,
    val body: Int = 0,
    val leftArm: Int = 0,
    val rightArm: Int = 0,
    val leftLeg: Int = 0,
    val rightLeg: Int = 0,
    val shield: Int = 0
) : Parcelable {
    init {
        listOf(head, body, leftArm, rightArm, leftLeg, rightLeg, shield).forEach {
            require(it in 0..99)
        }
    }
}