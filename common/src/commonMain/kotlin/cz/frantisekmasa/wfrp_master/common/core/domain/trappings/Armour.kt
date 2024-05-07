package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Armour(
    val head: Int = 0,
    val body: Int = 0,
    val leftArm: Int = 0,
    val rightArm: Int = 0,
    val leftLeg: Int = 0,
    val rightLeg: Int = 0,
    val shield: Int = 0,
) : Parcelable {
    init {
        listOf(head, body, leftArm, rightArm, leftLeg, rightLeg, shield).forEach {
            require(it in 0..99)
        }
    }

    @Stable
    fun armourPoints(location: HitLocation) =
        ArmourPoints(
            when (location) {
                HitLocation.HEAD -> head
                HitLocation.BODY -> body
                HitLocation.LEFT_ARM -> leftArm
                HitLocation.RIGHT_ARM -> rightArm
                HitLocation.LEFT_LEG -> leftLeg
                HitLocation.RIGHT_LEG -> rightLeg
            },
        )

    operator fun plus(other: Armour): Armour =
        Armour(
            head = (head + other.head).coerceAtMost(99),
            body = (body + other.body).coerceAtMost(99),
            leftArm = (leftArm + other.leftArm).coerceAtMost(99),
            rightArm = (rightArm + other.rightArm).coerceAtMost(99),
            leftLeg = (leftLeg + other.leftLeg).coerceAtMost(99),
            rightLeg = (rightLeg + other.rightLeg).coerceAtMost(99),
            shield = (shield + other.shield).coerceAtMost(99),
        )

    companion object {
        fun fromItems(items: List<InventoryItem>): Armour = fromArmourPieces(items) + fromWornShields(items)

        private fun fromWornShields(items: List<InventoryItem>): Armour {
            return items.asSequence()
                .map(InventoryItem::trappingType)
                .filterIsInstance<TrappingType.MeleeWeapon>()
                .filter { it.equipped != null }
                .sumOf { it.qualities[WeaponQuality.SHIELD] ?: 0 }
                .let { Armour(shield = it) }
        }

        private fun fromArmourPieces(items: List<InventoryItem>): Armour {
            return items.asSequence()
                .map(InventoryItem::trappingType)
                .filterIsInstance<TrappingType.Armour>()
                .filter { it.worn }
                .map {
                    Armour(
                        head = if (HitLocation.HEAD in it.locations) it.points.value else 0,
                        body = if (HitLocation.BODY in it.locations) it.points.value else 0,
                        leftArm = if (HitLocation.LEFT_ARM in it.locations) it.points.value else 0,
                        rightArm = if (HitLocation.RIGHT_ARM in it.locations) it.points.value else 0,
                        leftLeg = if (HitLocation.LEFT_LEG in it.locations) it.points.value else 0,
                        rightLeg = if (HitLocation.RIGHT_LEG in it.locations) it.points.value else 0,
                    )
                }.fold(Armour()) { a, b -> a + b }
        }
    }
}
