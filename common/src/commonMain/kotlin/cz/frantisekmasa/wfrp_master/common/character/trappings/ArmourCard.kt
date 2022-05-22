package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Armour
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTip
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTipCard
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


@Composable
internal fun ArmourCard(armour: TrappingsScreenModel.EquippedArmour, onChange: (Armour) -> Unit) {
    val hasLegacyArmour = !armour.legacyArmour.isZero()

    UserTipCard(
        if (hasLegacyArmour)
            UserTip.DEPRECATED_LEGACY_ARMOUR
        else UserTip.ARMOUR_TRAPPINGS,
        Modifier.padding(horizontal = 8.dp),
    )

    if (!hasLegacyArmour) {
        return
    }

    val change = { mutation: Armour.() -> Armour ->
        onChange(with(armour.legacyArmour, mutation))
    }

    val strings = LocalStrings.current.armour

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        CardTitle(strings.title)
        val modifier = Modifier.fillMaxWidth()

        Row(modifier = modifier) {
            ArmourPart(
                icon = Resources.Drawable.ArmorShield,
                name = strings.shield,
                base = armour.armourFromItems.shield,
                points = armour.legacyArmour.shield,
                onChange = { change { copy(shield = it) } },
                modifier = Modifier.weight(1f),
            )
            ArmourPart(
                icon = Resources.Drawable.ArmorHead,
                name = LocalStrings.current.combat.hitLocations.head,
                base = armour.armourFromItems.head,
                points = armour.legacyArmour.head,
                rollRange = 1..9,
                onChange = { change { copy(head = it) } },
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.weight(1f))
        }

        Row(modifier = modifier) {
            ArmourPart(
                icon = Resources.Drawable.ArmorArmRight,
                name = LocalStrings.current.combat.hitLocations.rightArm,
                base = armour.armourFromItems.rightArm,
                points = armour.legacyArmour.rightArm,
                rollRange = 25..44,
                onChange = { change { copy(rightArm = it) } },
                modifier = Modifier.weight(1f),
            )

            ArmourPart(
                icon = Resources.Drawable.ArmorChest,
                name = LocalStrings.current.combat.hitLocations.body,
                base = armour.armourFromItems.body,
                points = armour.legacyArmour.body,
                rollRange = 45..79,
                onChange = { change { copy(body = it) } },
                modifier = Modifier.weight(1f),
            )

            ArmourPart(
                icon = Resources.Drawable.ArmorArmLeft,
                name = LocalStrings.current.combat.hitLocations.leftArm,
                base = armour.armourFromItems.leftArm,
                points = armour.legacyArmour.leftArm,
                rollRange = 10..24,
                onChange = { change { copy(leftArm = it) } },
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = modifier
        ) {
            ArmourPart(
                icon = Resources.Drawable.ArmorLegRight,
                name = LocalStrings.current.combat.hitLocations.rightLeg,
                base = armour.armourFromItems.rightLeg,
                points = armour.legacyArmour.rightLeg,
                rollRange = 90..100,
                onChange = { change { copy(rightLeg = it) } },
            )

            ArmourPart(
                icon = Resources.Drawable.ArmorLegLeft,
                name = LocalStrings.current.combat.hitLocations.leftLeg,
                base = armour.armourFromItems.leftLeg,
                points = armour.legacyArmour.leftLeg,
                rollRange = 80..89,
                onChange = { change { copy(leftLeg = it) } },
            )
        }
    }
}

@Composable
private fun ArmourPart(
    icon: Resources.Drawable,
    name: String,
    points: Int,
    base: Int,
    modifier: Modifier = Modifier,
    rollRange: IntRange? = null,
    onChange: (Int) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Icon(drawableResource(icon), VisualOnlyIconDescription)
        NumberPicker(
            value = base + points,
            onIncrement = {
                if (points < Armour.MAX_VALUE) {
                    onChange(points + 1)
                }
            },
            onDecrement = {
                if (points > 0) {
                    onChange(points - 1)
                }
            },
        )

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(name)

            if (rollRange != null) {
                Text("${formatRoll(rollRange.first)} - ${formatRoll(rollRange.last)}")
            }
        }
    }
}

private fun formatRoll(roll: Int): String = (roll % 100).toString().padStart(2, '0')
