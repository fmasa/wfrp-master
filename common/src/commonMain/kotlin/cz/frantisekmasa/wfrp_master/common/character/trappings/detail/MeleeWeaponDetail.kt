package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun MeleeWeaponDetail(
    trapping: InventoryItem,
    meleeWeapon: TrappingType.MeleeWeapon,
    strengthBonus: Int?,
    onSaveRequest: suspend (InventoryItem) -> Unit,
) {

    if (strengthBonus == null) {
        FullScreenProgress()
        return
    }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        EquipBar(trapping, meleeWeapon, onSaveRequest)

        Column(Modifier.padding(Spacing.bodyPadding)) {
            val strings = LocalStrings.current

            SingleLineTextValue(
                strings.trappings.labelType,
                strings.trappings.types.meleeWeapon,
            )

            EncumbranceBox(trapping)

            SingleLineTextValue(
                strings.weapons.labelDamage,
                damageValue(meleeWeapon.damage, strengthBonus),
            )

            SingleLineTextValue(
                strings.weapons.labelGroup,
                meleeWeapon.group.localizedName,
            )

            SingleLineTextValue(
                strings.weapons.labelReach,
                meleeWeapon.reach.localizedName,
            )

            TrappingFeatures(
                meleeWeapon.qualities,
                meleeWeapon.flaws
            )

            if (trapping.quantity > 0) {
                SingleLineTextValue(
                    strings.trappings.labelQuantity,
                    trapping.quantity.toString(),
                )
            }

            TrappingDescription(trapping)
        }
    }
}
