package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.AmmunitionRangeExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AmmunitionDetailBody(
    subheadBar: @Composable ColumnScope.() -> Unit = {},
    damage: DamageExpression,
    range: AmmunitionRangeExpression,
    weaponGroups: Set<RangedWeaponGroup>,
    description: String,
    qualities: Map<WeaponQuality, Int>,
    flaws: Map<WeaponFlaw, Int>,
    encumbrance: Encumbrance,
    characterTrapping: InventoryItem?,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        subheadBar()

        Column(Modifier.padding(Spacing.bodyPadding)) {
            SingleLineTextValue(
                stringResource(Str.trappings_label_type),
                stringResource(Str.trappings_types_ammunition),
            )

            if (characterTrapping != null) {
                ItemQualitiesAndFlaws(characterTrapping)
            }

            EncumbranceBox(
                encumbrance = encumbrance,
                characterTrapping = characterTrapping,
            )

            SingleLineTextValue(stringResource(Str.weapons_label_damage), damage.formatted())

            SingleLineTextValue(stringResource(Str.weapons_label_range), range.formatted())

            val weaponGroupsNames = weaponGroups.map { it.localizedName }
            SingleLineTextValue(
                stringResource(Str.weapons_label_groups),
                remember(weaponGroupsNames) {
                    weaponGroupsNames.sorted().joinToString(", ")
                },
            )

            TrappingFeatures(qualities, flaws)

            TrappingDescription(description)
        }
    }
}
