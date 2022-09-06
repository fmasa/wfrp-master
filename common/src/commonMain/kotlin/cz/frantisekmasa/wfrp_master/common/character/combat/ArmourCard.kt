package cz.frantisekmasa.wfrp_master.common.character.combat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.character.combat.CharacterCombatScreenModel.WornArmourPiece
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourPoints
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Chip
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTip
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTipCard
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun ArmourCard(
    armour: Armour,
    armourPieces: Map<HitLocation, List<WornArmourPiece>>,
    toughnessBonus: Int,
    onTrappingClick: (InventoryItem) -> Unit,
) {
    UserTipCard(UserTip.ARMOUR_TRAPPINGS, Modifier.padding(horizontal = 8.dp))

    CardContainer(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
    ) {
        CardTitle(LocalStrings.current.armour.title)

        Row(
            modifier = Modifier.padding(top = Spacing.large),
            horizontalArrangement = Arrangement.spacedBy(
                Spacing.large,
                Alignment.CenterHorizontally
            ),
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    LocalStrings.current.characteristics.toughnessBonusShortcut,
                    Modifier.padding(end = Spacing.medium),
                )
                Points(toughnessBonus)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f),
            ) {
                Points(armour.shield)
                Text(
                    LocalStrings.current.armour.shield,
                    Modifier.padding(start = Spacing.medium),
                )
            }
        }

        val locations = remember { HitLocation.values().sortedBy { it.rollRange.first } }

        locations.forEach { location ->
            key(location) {
                Location(
                    location = location,
                    points = armour.armourPoints(location),
                    trappings = armourPieces[location] ?: emptyList(),
                    onTrappingClick = onTrappingClick,
                )
            }
        }
    }
}

@Composable
@Stable
private fun formatRoll(roll: Int): String {
    if (roll == 100) {
        return "00"
    }

    return roll.toString().padStart(2, '0')
}

@Composable
private fun Points(value: Int) {
    Chip(padding = Spacing.tiny) {
        Text(value.toString())
    }
}

@Composable
private fun Location(
    location: HitLocation,
    points: ArmourPoints,
    trappings: List<WornArmourPiece>,
    onTrappingClick: (InventoryItem) -> Unit,
) {
    val rollRange = location.rollRange
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.large, top = Spacing.large)
            .let {
                if (trappings.isNotEmpty())
                    it.clickable { expanded = !expanded }
                else it
            }
    ) {
        Row(Modifier.weight(1f)) {
            Row {
                Text(
                    "${formatRoll(rollRange.first)}-${formatRoll(rollRange.last)}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = Spacing.medium),
                )

                Text(location.localizedName)
            }

            if (trappings.isNotEmpty()) {
                Icon(
                    if (expanded)
                        Icons.Rounded.ExpandLess
                    else Icons.Rounded.ExpandMore,
                    null,
                )
            }
        }

        Points(points.value)
    }

    Column(Modifier.animateContentSize()) {
        if (!expanded) {
            return@Column
        }

        trappings.forEach { item ->
            key(item.trapping.id) {
                val armour = item.armour

                ListItem(
                    modifier = Modifier.clickable { onTrappingClick(item.trapping) },
                    text = { Text(item.trapping.name) },
                    secondaryText = if (armour.qualities.isNotEmpty() || armour.flaws.isNotEmpty())
                        (
                            {
                                TrappingFeatureList(
                                    armour.qualities,
                                    armour.flaws,
                                    Modifier.fillMaxWidth()
                                )
                            }
                            )
                    else null,
                    trailing = {
                        Text(
                            text = armour.points.value.toString(),
                            style = MaterialTheme.typography.body2,
                        )
                    }
                )
            }
        }
    }
}
