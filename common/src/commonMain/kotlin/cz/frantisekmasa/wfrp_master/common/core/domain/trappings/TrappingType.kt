package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

typealias Rating = Int

@Serializable
@JsonClassDiscriminator("kind")
@Immutable
sealed class TrappingType : Parcelable {
    interface WearableTrapping {
        val worn: Boolean
    }

    sealed class Weapon : TrappingType() {
        abstract val damage: DamageExpression
        abstract val qualities: Map<WeaponQuality, Rating>
        abstract val flaws: Map<WeaponFlaw, Rating>
        abstract val equipped: WeaponEquip?
    }

    @Parcelize
    @Serializable
    @SerialName("MELEE_WEAPON")
    @Immutable
    data class MeleeWeapon(
        val group: MeleeWeaponGroup,
        val reach: Reach,
        override val damage: DamageExpression,
        override val qualities: Map<WeaponQuality, Rating>,
        override val flaws: Map<WeaponFlaw, Rating>,
        override val equipped: WeaponEquip?,
    ) : Weapon()

    @Parcelize
    @Serializable
    @SerialName("RANGED_WEAPON")
    @Immutable
    data class RangedWeapon(
        val group: RangedWeaponGroup,
        val range: WeaponRangeExpression,
        override val damage: DamageExpression,
        override val qualities: Map<WeaponQuality, Rating>,
        override val flaws: Map<WeaponFlaw, Rating>,
        override val equipped: WeaponEquip?,
    ) : Weapon()

    @Parcelize
    @Serializable
    @SerialName("AMMUNITION")
    @Immutable
    data class Ammunition(
        val weaponGroups: Set<RangedWeaponGroup>,
        val range: AmmunitionRangeExpression,
        val qualities: Map<WeaponQuality, Rating>,
        val flaws: Map<WeaponFlaw, Rating>,
        val damage: DamageExpression,
    ) : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("ARMOUR")
    @Immutable
    data class Armour(
        val locations: Set<HitLocation>,
        val type: ArmourType,
        val points: ArmourPoints,
        val qualities: Map<ArmourQuality, Rating> = emptyMap(),
        val flaws: Map<ArmourFlaw, Rating> = emptyMap(),
        override val worn: Boolean,
    ) : TrappingType(), WearableTrapping

    @Parcelize
    @Serializable
    @SerialName("CONTAINER")
    @Immutable
    data class Container(
        val carries: Encumbrance,
        override val worn: Boolean,
    ) : TrappingType(), WearableTrapping
}
