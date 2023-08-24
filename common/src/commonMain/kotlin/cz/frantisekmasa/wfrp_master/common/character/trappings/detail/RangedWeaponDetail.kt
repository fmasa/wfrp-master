package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponRangeExpression
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun RangedWeaponDetailBody(
    subheadBar: @Composable ColumnScope.() -> Unit = {},
    damage: DamageExpression,
    range: WeaponRangeExpression,
    group: RangedWeaponGroup,
    qualities: Map<WeaponQuality, Int>,
    flaws: Map<WeaponFlaw, Int>,
    strengthBonus: Int?,
    description: String,
    encumbrance: Encumbrance,
    characterTrapping: InventoryItem?,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        subheadBar()

        Column(Modifier.padding(Spacing.bodyPadding)) {
            SingleLineTextValue(
                stringResource(Str.trappings_label_type),
                stringResource(Str.trappings_types_ranged_weapon),
            )

            if (characterTrapping != null) {
                ItemQualitiesAndFlaws(characterTrapping)
            }

            EncumbranceBox(encumbrance, characterTrapping)

            SingleLineTextValue(
                stringResource(Str.weapons_label_damage),
                damageValue(damage, strengthBonus = strengthBonus),
            )

            SingleLineTextValue(
                stringResource(Str.weapons_label_group),
                group.localizedName,
            )

            SingleLineTextValue(
                stringResource(Str.weapons_label_range),
                range.value,
            )

            TrappingFeatures(qualities, flaws)

            if (characterTrapping != null && characterTrapping.quantity > 0) {
                SingleLineTextValue(
                    stringResource(Str.trappings_label_quantity),
                    characterTrapping.quantity.toString(),
                )
            }

            TrappingDescription(description)
        }
    }
}
