package cz.muni.fi.rpg.ui.character.inventory

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope.weight
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.ui.common.composables.CardContainer
import cz.muni.fi.rpg.ui.common.composables.CardTitle
import cz.muni.fi.rpg.ui.common.composables.NumberPicker


@Composable
internal fun ArmorCard(armor: Armor, onChange: (Armor) -> Unit) {
    val change = { mutation: Armor.() -> Armor -> onChange(with(armor, mutation)) }

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        CardTitle(R.string.title_armor)
        val modifier = Modifier.fillMaxWidth()

        Row(modifier = modifier) {
            ArmorPart(
                iconRes = R.drawable.ic_armor_shield,
                nameRes = R.string.armor_shield,
                points = armor.shield,
                onChange = { change { copy(shield = it) } },
            )
            ArmorPart(
                iconRes = R.drawable.ic_armor_head,
                nameRes = R.string.armor_head,
                points = armor.head,
                rollRange = 1..9,
                onChange = { change { copy(head = it) } },
            )
            Spacer(Modifier.weight(1f))
        }


        Row(modifier = modifier) {
            ArmorPart(
                iconRes = R.drawable.ic_armor_arm_right,
                nameRes = R.string.armor_right_arm,
                points = armor.rightArm,
                rollRange = 25..44,
                onChange = { change { copy(rightArm = it) } },
            )

            ArmorPart(
                iconRes = R.drawable.ic_armor_chest,
                nameRes = R.string.armor_body,
                points = armor.body,
                rollRange = 45..79,
                onChange = { change { copy(body = it) } },
            )

            ArmorPart(
                iconRes = R.drawable.ic_armor_arm_left,
                nameRes = R.string.armor_left_arm,
                points = armor.leftArm,
                rollRange = 10..24,
                onChange = { change { copy(leftArm = it) } },
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            modifier = modifier
        ) {
            ArmorPart(
                iconRes = R.drawable.ic_armor_leg_right,
                nameRes = R.string.armor_right_leg,
                points = armor.rightLeg,
                rollRange = 90..100,
                onChange = { change { copy(rightLeg = it) } },
                modifier = Modifier,
            )

            ArmorPart(
                iconRes = R.drawable.ic_armor_leg_left,
                nameRes = R.string.armor_left_leg,
                points = armor.leftLeg,
                rollRange = 80..89,
                onChange = { change { copy(leftLeg = it) } },
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun ArmorPart(
    @DrawableRes iconRes: Int,
    @StringRes nameRes: Int,
    points: Int,
    rollRange: IntRange? = null,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier.weight(1f)
) {
    Column(horizontalGravity = Alignment.CenterHorizontally, modifier = modifier) {
        Icon(vectorResource(iconRes))
        NumberPicker(
            value = points,
            onIncrement = {
                if (points < Armor.MAX_VALUE) {
                    onChange(points + 1)
                }
            },
            onDecrement = {
                if (points > 0) {
                    onChange(points - 1)
                }
            },
        )

        ProvideEmphasis(EmphasisAmbient.current.medium) {
            Text(stringResource(nameRes))
            if (rollRange != null) {
                Text("${formatRoll(rollRange.first)} - ${formatRoll(rollRange.last)}")
            }
        }
    }
}

private fun formatRoll(roll: Int): String = (roll % 100).toString().padStart(2, '0')