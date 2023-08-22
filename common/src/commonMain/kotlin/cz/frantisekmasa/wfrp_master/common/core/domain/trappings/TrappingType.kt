package cz.frantisekmasa.wfrp_master.common.core.domain.trappings

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingTypeOption
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType as CompendiumTrappingType

typealias Rating = Int

@Serializable
@JsonClassDiscriminator("kind")
@Immutable
sealed class TrappingType : Parcelable {

    fun updateFromCompendium(trappingType: CompendiumTrappingType?): TrappingType? {
        val updatedTrappingType = fromCompendium(trappingType)

        if (updatedTrappingType is WearableTrapping && this is WearableTrapping && worn) {
            return updatedTrappingType.takeOn()
        }

        if (updatedTrappingType is Weapon && this is Weapon && equipped != null) {
            return updatedTrappingType.equip(equipped!!)
        }

        return updatedTrappingType
    }

    companion object {
        fun fromCompendium(trappingType: CompendiumTrappingType?): TrappingType? {
            return when (trappingType) {
                null -> null
                is CompendiumTrappingType.Armour -> Armour(
                    locations = trappingType.locations,
                    type = trappingType.type,
                    points = trappingType.points,
                    qualities = trappingType.qualities,
                    flaws = trappingType.flaws,
                    worn = false,
                )

                is CompendiumTrappingType.Ammunition -> Ammunition(
                    weaponGroups = trappingType.weaponGroups,
                    range = trappingType.range,
                    qualities = trappingType.qualities,
                    flaws = trappingType.flaws,
                    damage = trappingType.damage,
                )

                is CompendiumTrappingType.BookOrDocument -> BookOrDocument
                is CompendiumTrappingType.ClothingOrAccessory -> ClothingOrAccessory(worn = false)
                is CompendiumTrappingType.Container -> Container(
                    carries = trappingType.carries,
                    worn = false,
                )

                is CompendiumTrappingType.DrugOrPoison -> DrugOrPoison
                is CompendiumTrappingType.FoodOrDrink -> FoodOrDrink
                is CompendiumTrappingType.HerbOrDraught -> HerbOrDraught
                is CompendiumTrappingType.MeleeWeapon -> MeleeWeapon(
                    group = trappingType.group,
                    reach = trappingType.reach,
                    damage = trappingType.damage,
                    qualities = trappingType.qualities,
                    flaws = trappingType.flaws,
                    equipped = null,
                )

                is CompendiumTrappingType.RangedWeapon -> RangedWeapon(
                    group = trappingType.group,
                    range = trappingType.range,
                    damage = trappingType.damage,
                    qualities = trappingType.qualities,
                    flaws = trappingType.flaws,
                    equipped = null,
                )

                CompendiumTrappingType.SpellIngredient -> SpellIngredient
                CompendiumTrappingType.ToolOrKit -> ToolOrKit
                CompendiumTrappingType.TradeTools -> TradeTools
            }
        }
    }

    sealed class WearableTrapping : TrappingType() {
        abstract val worn: Boolean

        abstract fun takeOff(): WearableTrapping
        abstract fun takeOn(): WearableTrapping
    }

    sealed class Weapon : TrappingType() {
        abstract val damage: DamageExpression
        abstract val qualities: Map<WeaponQuality, Rating>
        abstract val flaws: Map<WeaponFlaw, Rating>
        abstract val equipped: WeaponEquip?

        abstract fun equip(equip: WeaponEquip): Weapon
        abstract fun unequip(): Weapon
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
    ) : Weapon() {
        override fun equip(equip: WeaponEquip): MeleeWeapon = copy(equipped = equip)
        override fun unequip(): MeleeWeapon = copy(equipped = null)
    }

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
    ) : Weapon() {
        override fun equip(equip: WeaponEquip): RangedWeapon = copy(equipped = equip)
        override fun unequip(): RangedWeapon = copy(equipped = null)
    }

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
    ) : WearableTrapping() {

        override fun takeOff(): Armour = copy(worn = false)
        override fun takeOn(): Armour = copy(worn = true)
    }

    @Parcelize
    @Serializable
    @SerialName("CONTAINER")
    @Immutable
    data class Container(
        val carries: Encumbrance,
        override val worn: Boolean,
    ) : WearableTrapping() {

        override fun takeOff(): Container = copy(worn = false)
        override fun takeOn(): Container = copy(worn = true)
    }

    @Parcelize
    @Serializable
    @SerialName("CLOTHING_OR_ACCESSORY")
    data class ClothingOrAccessory(
        override val worn: Boolean,
    ) : WearableTrapping() {
        override fun takeOff(): ClothingOrAccessory = copy(worn = false)
        override fun takeOn(): ClothingOrAccessory = copy(worn = true)
    }

    @Parcelize
    @Serializable
    @SerialName("FOOD_OR_DRINK")
    object FoodOrDrink : SimpleTrapping() {
        override val name get() = TrappingTypeOption.FOOD_OR_DRINK.translatableName
    }

    @Parcelize
    @Serializable
    @SerialName("TOOL_OR_KIT")
    object ToolOrKit : SimpleTrapping() {
        override val name get() = TrappingTypeOption.TOOL_OR_KIT.translatableName
    }

    @Parcelize
    @Serializable
    @SerialName("BOOKS_OR_DOCUMENT")
    object BookOrDocument : SimpleTrapping() {
        override val name get() = TrappingTypeOption.BOOK_OR_DOCUMENT.translatableName
    }

    @Parcelize
    @Serializable
    @SerialName("TRADE_TOOLS")
    object TradeTools : SimpleTrapping() {
        override val name get() = TrappingTypeOption.TRADE_TOOLS.translatableName
    }

    @Parcelize
    @Serializable
    @SerialName("DRUG_OR_POISON")
    object DrugOrPoison : SimpleTrapping() {
        override val name get() = TrappingTypeOption.DRUG_OR_POISON.translatableName
    }

    @Parcelize
    @Serializable
    @SerialName("HERB_OR_DRAUGHT")
    object HerbOrDraught : SimpleTrapping() {
        override val name get() = TrappingTypeOption.HERB_OR_DRAUGHT.translatableName
    }

    @Parcelize
    @Serializable
    @SerialName("SPELL_INGREDIENT")
    object SpellIngredient : SimpleTrapping() {
        override val name get() = TrappingTypeOption.SPELL_INGREDIENT.translatableName
    }

    sealed class SimpleTrapping : TrappingType() {
        abstract val name: StringResource
    }
}
