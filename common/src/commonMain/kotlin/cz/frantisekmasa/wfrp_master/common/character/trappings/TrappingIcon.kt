package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources

@Composable
@Stable
fun trappingIcon(trappingType: TrappingType?) = when (trappingType) {
    is TrappingType.Ammunition -> Resources.Drawable.TrappingAmmunition
    is TrappingType.Armour -> Resources.Drawable.TrappingArmour
    is TrappingType.BookOrDocument -> Resources.Drawable.TrappingBookOrDocument
    is TrappingType.MeleeWeapon -> Resources.Drawable.WeaponSkill
    is TrappingType.ClothingOrAccessory -> Resources.Drawable.TrappingClothingOrAccessory
    is TrappingType.Container -> Resources.Drawable.TrappingContainer
    is TrappingType.DrugOrPoison -> Resources.Drawable.TrappingDrugOrPoison
    is TrappingType.FoodOrDrink -> Resources.Drawable.TrappingFoodOrDrink
    is TrappingType.HerbOrDraught -> Resources.Drawable.TrappingHerbOrDraught
    is TrappingType.RangedWeapon -> Resources.Drawable.BallisticSkill
    is TrappingType.SpellIngredient -> Resources.Drawable.Spell
    is TrappingType.ToolOrKit -> Resources.Drawable.TrappingTool
    is TrappingType.TradeTools -> Resources.Drawable.TrappingTool
    null -> Resources.Drawable.TrappingMiscellaneous
}
