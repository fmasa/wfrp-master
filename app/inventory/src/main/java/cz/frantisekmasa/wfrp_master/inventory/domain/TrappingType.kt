package cz.frantisekmasa.wfrp_master.inventory.domain

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import cz.frantisekmasa.wfrp_master.inventory.domain.armour.ArmourLocation
import cz.frantisekmasa.wfrp_master.inventory.domain.armour.ArmourPoints
import cz.frantisekmasa.wfrp_master.inventory.domain.armour.ArmourType
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.AmmunitionRangeExpression
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.DamageExpression
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.Reach
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.WeaponEquip
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.WeaponFlaw
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.WeaponQuality
import cz.frantisekmasa.wfrp_master.inventory.domain.weapon.WeaponRangeExpression
import kotlinx.parcelize.Parcelize

typealias Rating = Int

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
sealed class TrappingType : Parcelable {
    interface WearableTrapping {
        val worn: Boolean
    }

    @Parcelize
    @JsonTypeName("MELEE_WEAPON")
    data class MeleeWeapon(
        val group: MeleeWeaponGroup,
        val reach: Reach,
        val damage: DamageExpression,
        val qualities: Map<WeaponQuality, Rating>,
        val flaws: Map<WeaponFlaw, Rating>,
        val equipped: WeaponEquip?,
    ) : TrappingType()

    @Parcelize
    @JsonTypeName("RANGED_WEAPON")
    data class RangedWeapon(
        val group: RangedWeaponGroup,
        val range: WeaponRangeExpression,
        val damage: DamageExpression,
        val qualities: Map<WeaponQuality, Rating>,
        val flaws: Map<WeaponFlaw, Rating>,
        val equipped: WeaponEquip?,
    ) : TrappingType()

    @Parcelize
    @JsonTypeName("AMMUNITION")
    data class Ammunition(
        val weaponGroups: Set<RangedWeaponGroup>,
        val range: AmmunitionRangeExpression,
        val qualities: Map<WeaponQuality, Rating>,
        val flaws: Map<WeaponFlaw, Rating>,
        val damage: DamageExpression,
    ) : TrappingType()

    @Parcelize
    @JsonTypeName("ARMOUR")
    data class Armour(
        val locations: Set<ArmourLocation>,
        val type: ArmourType,
        val points: ArmourPoints,
        override val worn: Boolean,
    ) : TrappingType(), WearableTrapping

    @Parcelize
    @JsonTypeName("CONTAINER")
    data class Container(
        val carries: Encumbrance,
        override val worn: Boolean,
    ) : TrappingType(), WearableTrapping
}
