package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun RangedWeaponDetail(
    trapping: InventoryItem,
    rangedWeapon: TrappingType.RangedWeapon,
    strengthBonus: Int?,
    onSaveRequest: suspend (InventoryItem) -> Unit,
) {
    if (strengthBonus == null) {
        FullScreenProgress()
        return
    }

    Column(Modifier.verticalScroll(rememberScrollState())) {
        EquipBar(trapping, rangedWeapon, onSaveRequest)

        Column(Modifier.padding(Spacing.bodyPadding)) {
            SingleLineTextValue(
                stringResource(Str.trappings_label_type),
                stringResource(Str.trappings_types_ranged_weapon),
            )

            EncumbranceBox(trapping)

            SingleLineTextValue(
                stringResource(Str.weapons_label_damage),
                damageValue(rangedWeapon.damage, strengthBonus),
            )

            SingleLineTextValue(
                stringResource(Str.weapons_label_group),
                rangedWeapon.group.localizedName,
            )

            SingleLineTextValue(
                stringResource(Str.weapons_label_range),
                rangedWeapon.range.value,
            )

            TrappingFeatures(
                rangedWeapon.qualities,
                rangedWeapon.flaws
            )

            if (trapping.quantity > 0) {
                SingleLineTextValue(
                    stringResource(Str.trappings_label_quantity),
                    trapping.quantity.toString(),
                )
            }

            TrappingDescription(trapping)
        }
    }
}
