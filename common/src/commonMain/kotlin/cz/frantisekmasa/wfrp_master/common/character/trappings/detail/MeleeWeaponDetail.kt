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
            SingleLineTextValue(
                stringResource(Str.trappings_label_type),
                stringResource(Str.trappings_types_melee_weapon),
            )

            EncumbranceBox(trapping)

            SingleLineTextValue(
                stringResource(Str.weapons_label_damage),
                damageValue(meleeWeapon.damage, strengthBonus),
            )

            SingleLineTextValue(
                stringResource(Str.weapons_label_group),
                meleeWeapon.group.localizedName,
            )

            SingleLineTextValue(
                stringResource(Str.weapons_label_reach),
                meleeWeapon.reach.localizedName,
            )

            TrappingFeatures(
                meleeWeapon.qualities,
                meleeWeapon.flaws
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
