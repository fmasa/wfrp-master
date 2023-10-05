package cz.frantisekmasa.wfrp_master.common.character.combat

import cz.frantisekmasa.wfrp_master.common.combat.domain.EquippedWeapon
import cz.frantisekmasa.wfrp_master.common.combat.domain.WornArmourPiece
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponEquip
import kotlinx.collections.immutable.ImmutableMap

data class CharacterCombatScreenState(
    val toughnessBonus: Int,
    val equippedWeapons: ImmutableMap<WeaponEquip, List<EquippedWeapon>>,
    val armourPoints: Armour,
    val armourPieces: ImmutableMap<HitLocation, List<WornArmourPiece>>,
)
