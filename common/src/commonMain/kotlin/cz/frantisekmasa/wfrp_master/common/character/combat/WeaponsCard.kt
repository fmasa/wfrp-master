package cz.frantisekmasa.wfrp_master.common.character.combat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.trappingIcon
import cz.frantisekmasa.wfrp_master.common.combat.domain.EquippedWeapon
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponEquip
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun WeaponsCard(
    equips: List<Pair<WeaponEquip, List<EquippedWeapon>>>,
    onTrappingClick: (InventoryItem) -> Unit,
) {
    CardContainer(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
    ) {
        CardTitle(stringResource(Str.character_title_weapons))

        if (equips.isEmpty()) {
            EmptyUI(
                text = stringResource(Str.character_messages_no_equipped_weapons),
                subText = stringResource(Str.character_messages_no_equipped_weapons_sub_text),
                icon = Resources.Drawable.WeaponSkill,
                size = EmptyUI.Size.Small,
            )

            return@CardContainer
        }

        equips.forEach { (equip, weapons) ->
            key(equip) {
                CardSubtitle(equip.localizedName)
                WeaponList(weapons, onTrappingClick)
            }
        }
    }
}

@Composable
private fun WeaponList(weapons: List<EquippedWeapon>, onTrappingClick: (InventoryItem) -> Unit) {
    weapons.forEach { equippedWeapon ->
        key(equippedWeapon.trapping.id) {
            val weapon = equippedWeapon.weapon

            ListItem(
                modifier = Modifier.clickable { onTrappingClick(equippedWeapon.trapping) },
                icon = { ItemIcon(trappingIcon(equippedWeapon.weapon), ItemIcon.Size.Small) },
                text = { Text(equippedWeapon.trapping.name) },
                secondaryText = if (
                    weapon.qualities.isNotEmpty() ||
                    weapon.flaws.isNotEmpty() ||
                    equippedWeapon.trapping.itemQualities.isNotEmpty() ||
                    equippedWeapon.trapping.itemFlaws.isNotEmpty()
                )
                    (
                        {
                            TrappingFeatureList(
                                equippedWeapon.trapping,
                                weapon.qualities,
                                weapon.flaws,
                                Modifier.fillMaxWidth()
                            )
                        }
                        )
                else null,
                trailing = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "+${equippedWeapon.damage.value}",
                            style = MaterialTheme.typography.body1,
                            fontWeight = FontWeight.Bold,
                        )

                        val damageExpression = weapon.damage.formatted()
                        Text(
                            if (damageExpression.startsWith("+"))
                                damageExpression
                            else "+$damageExpression",
                            style = MaterialTheme.typography.overline
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CardSubtitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = Spacing.large, top = Spacing.medium),
        style = MaterialTheme.typography.caption,
        fontWeight = FontWeight.Bold,
    )
}
