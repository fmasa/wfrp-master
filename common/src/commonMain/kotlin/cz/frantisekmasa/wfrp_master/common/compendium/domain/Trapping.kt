package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.AmmunitionRangeExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourPoints
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Rating
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Reach
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponRangeExpression
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Parcelize
@Serializable
@Immutable
data class Trapping(
    @Contextual override val id: Uuid,
    override val name: String,
    val trappingType: TrappingType?,
    val description: String,
    val encumbrance: Encumbrance,
    val availability: Availability,
    val packSize: Int,
    val price: Money,
    override val isVisibleToPlayers: Boolean,
) : CompendiumItem<Trapping>() {
    init {
        require(name.isNotBlank())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
        require(packSize >= 1) { "Pack size must be a positive number" }
    }

    override fun replace(original: Trapping) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())

    override fun changeVisibility(isVisibleToPlayers: Boolean) =
        copy(isVisibleToPlayers = isVisibleToPlayers)

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 2500
    }
}

@Serializable
@JsonClassDiscriminator("kind")
@Immutable
sealed class TrappingType : Parcelable {
    sealed class Weapon : TrappingType() {
        abstract val damage: DamageExpression
        abstract val qualities: Map<WeaponQuality, Rating>
        abstract val flaws: Map<WeaponFlaw, Rating>
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
    ) : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("CONTAINER")
    @Immutable
    data class Container(
        val carries: Encumbrance,
    ) : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("CLOTHING_OR_ACCESSORY")
    object ClothingOrAccessory : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("FOOD_OR_DRINK")
    object FoodOrDrink : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("TOOL_OR_KIT")
    object ToolOrKit : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("BOOKS_OR_DOCUMENT")
    object BookOrDocument : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("TRADE_TOOLS")
    object TradeTools : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("DRUG_OR_POISON")
    object DrugOrPoison : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("HERB_OR_DRAUGHT")
    object HerbOrDraught : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("SPELL_INGREDIENT")
    object SpellIngredient : TrappingType()

    @Parcelize
    @Serializable
    @SerialName("PROSTHETIC")
    object Prosthetic : TrappingType()
}
