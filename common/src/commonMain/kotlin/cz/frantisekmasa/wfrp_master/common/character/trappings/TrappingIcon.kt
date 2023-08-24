package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType as CompendiumTrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType as CharacterTrappingType

@Composable
@Stable
fun trappingIcon(trappingType: CharacterTrappingType?) = when (trappingType) {
    is CharacterTrappingType.Ammunition -> Resources.Drawable.TrappingAmmunition
    is CharacterTrappingType.Armour -> Resources.Drawable.TrappingArmour
    is CharacterTrappingType.BookOrDocument -> Resources.Drawable.TrappingBookOrDocument
    is CharacterTrappingType.MeleeWeapon -> Resources.Drawable.WeaponSkill
    is CharacterTrappingType.ClothingOrAccessory -> Resources.Drawable.TrappingClothingOrAccessory
    is CharacterTrappingType.Container -> Resources.Drawable.TrappingContainer
    is CharacterTrappingType.DrugOrPoison -> Resources.Drawable.TrappingDrugOrPoison
    is CharacterTrappingType.FoodOrDrink -> Resources.Drawable.TrappingFoodOrDrink
    is CharacterTrappingType.HerbOrDraught -> Resources.Drawable.TrappingHerbOrDraught
    is CharacterTrappingType.Prosthetic -> Resources.Drawable.TrappingProsthetic
    is CharacterTrappingType.RangedWeapon -> Resources.Drawable.BallisticSkill
    is CharacterTrappingType.SpellIngredient -> Resources.Drawable.Spell
    is CharacterTrappingType.ToolOrKit -> Resources.Drawable.TrappingTool
    is CharacterTrappingType.TradeTools -> Resources.Drawable.TrappingTool
    null -> Resources.Drawable.TrappingMiscellaneous
}

@Composable
@Stable
fun trappingIcon(trappingType: CompendiumTrappingType?) = when (trappingType) {
    is CompendiumTrappingType.Ammunition -> Resources.Drawable.TrappingAmmunition
    is CompendiumTrappingType.Armour -> Resources.Drawable.TrappingArmour
    is CompendiumTrappingType.BookOrDocument -> Resources.Drawable.TrappingBookOrDocument
    is CompendiumTrappingType.MeleeWeapon -> Resources.Drawable.WeaponSkill
    is CompendiumTrappingType.ClothingOrAccessory -> Resources.Drawable.TrappingClothingOrAccessory
    is CompendiumTrappingType.Container -> Resources.Drawable.TrappingContainer
    is CompendiumTrappingType.DrugOrPoison -> Resources.Drawable.TrappingDrugOrPoison
    is CompendiumTrappingType.FoodOrDrink -> Resources.Drawable.TrappingFoodOrDrink
    is CompendiumTrappingType.HerbOrDraught -> Resources.Drawable.TrappingHerbOrDraught
    is CompendiumTrappingType.Prosthetic -> Resources.Drawable.TrappingProsthetic
    is CompendiumTrappingType.RangedWeapon -> Resources.Drawable.BallisticSkill
    is CompendiumTrappingType.SpellIngredient -> Resources.Drawable.Spell
    is CompendiumTrappingType.ToolOrKit -> Resources.Drawable.TrappingTool
    is CompendiumTrappingType.TradeTools -> Resources.Drawable.TrappingTool
    null -> Resources.Drawable.TrappingMiscellaneous
}
