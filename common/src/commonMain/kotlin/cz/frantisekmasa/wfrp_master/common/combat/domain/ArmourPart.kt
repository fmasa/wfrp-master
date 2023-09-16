package cz.frantisekmasa.wfrp_master.common.combat.domain

import androidx.compose.runtime.Immutable
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourPoints

@Immutable
data class ArmourPart(
    val hitLocation: HitLocation,
    val armourPieces: List<WornArmourPiece>,
) {
    val points get() = ArmourPoints(armourPieces.sumOf { it.armour.points.value })
}
