package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.journal.rules.TrappingJournal
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Reach
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MeleeWeaponDetailBody(
    subheadBar: @Composable ColumnScope.() -> Unit = {},
    damage: DamageExpression,
    reach: Reach,
    group: MeleeWeaponGroup,
    qualities: Map<WeaponQuality, Int>,
    flaws: Map<WeaponFlaw, Int>,
    strengthBonus: Int?,
    description: String,
    encumbrance: Encumbrance,
    trappingJournal: TrappingJournal,
    characterTrapping: InventoryItem?,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        subheadBar()

        SelectionContainer {
            Column(Modifier.padding(Spacing.bodyPadding)) {
                SingleLineTextValue(
                    stringResource(Str.trappings_label_type),
                    stringResource(Str.trappings_types_melee_weapon),
                )

                if (characterTrapping != null) {
                    ItemQualitiesAndFlaws(characterTrapping, trappingJournal)
                }

                EncumbranceBox(encumbrance, characterTrapping)

                SingleLineTextValue(
                    stringResource(Str.weapons_label_damage),
                    damageValue(damage, strengthBonus),
                )

                SingleLineTextValue(
                    stringResource(Str.weapons_label_group),
                    group.localizedName,
                )

                SingleLineTextValue(
                    stringResource(Str.weapons_label_reach),
                    reach.localizedName,
                )

                TrappingFeatures(qualities, flaws, trappingJournal.weaponQualities, trappingJournal.weaponFlaws)

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
}
