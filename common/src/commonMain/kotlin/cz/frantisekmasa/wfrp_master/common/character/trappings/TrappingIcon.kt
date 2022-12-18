package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources

@Composable
@Stable
fun trappingIcon(trappingType: TrappingType?) = when (trappingType) {
    is TrappingType.Ammunition -> Resources.Drawable.TrappingAmmunition
    is TrappingType.Armour -> Resources.Drawable.ArmorChest
    is TrappingType.MeleeWeapon -> Resources.Drawable.WeaponSkill
    is TrappingType.Container -> Resources.Drawable.TrappingContainer
    is TrappingType.RangedWeapon -> Resources.Drawable.BallisticSkill
    null -> Resources.Drawable.TrappingMiscellaneous
}
