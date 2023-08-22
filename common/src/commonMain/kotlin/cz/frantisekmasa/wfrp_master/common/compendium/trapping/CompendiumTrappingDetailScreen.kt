package cz.frantisekmasa.wfrp_master.common.compendium.trapping

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.MoneyBalance
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingTypeOption
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.AmmunitionDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.ArmourDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.EncumbranceBox
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.MeleeWeaponDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.RangedWeaponDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.SimpleTrappingDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.TrappingDescription
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

class CompendiumTrappingDetailScreen(
    private val partyId: PartyId,
    private val trappingId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: TrappingCompendiumScreenModel = rememberScreenModel(arg = partyId)

        CompendiumItemDetailScreen(
            id = trappingId,
            screenModel = screenModel,
            scrollable = false, // Trapping details are by themself scrollable
            detail = { trapping ->
                val subheadBar: @Composable ColumnScope.() -> Unit = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.bodyPadding),
                    ) {
                        MoneyBalance(trapping.price)
                        SingleLineTextValue(
                            stringResource(Str.trappings_label_availability),
                            trapping.availability.localizedName,
                        )
                    }
                }
                when (val type = trapping.trappingType) {
                    is TrappingType.Ammunition -> {
                        AmmunitionDetailBody(
                            subheadBar = subheadBar,
                            damage = type.damage,
                            range = type.range,
                            weaponGroups = type.weaponGroups,
                            description = trapping.description,
                            qualities = type.qualities,
                            flaws = type.flaws,
                            encumbrance = trapping.encumbrance,
                            characterTrapping = null,
                        )
                    }

                    is TrappingType.Armour -> {
                        ArmourDetailBody(
                            subheadBar = subheadBar,
                            locations = type.locations,
                            points = type.points,
                            qualities = type.qualities,
                            flaws = type.flaws,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    TrappingType.BookOrDocument -> {
                        SimpleTrappingDetailBody(
                            subheadBar = subheadBar,
                            trappingType = TrappingTypeOption.BOOK_OR_DOCUMENT.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    is TrappingType.Container -> {
                        Column(Modifier.padding(Spacing.bodyPadding)) {
                            SingleLineTextValue(
                                stringResource(Str.trappings_label_type),
                                TrappingTypeOption.CONTAINER.localizedName,
                            )

                            SingleLineTextValue(
                                stringResource(Str.trappings_label_carries),
                                type.carries.toString(),
                            )

                            EncumbranceBox(trapping.encumbrance, null)

                            TrappingDescription(trapping.description)
                        }
                    }

                    is TrappingType.ClothingOrAccessory -> {
                        SimpleTrappingDetailBody(
                            subheadBar = subheadBar,
                            trappingType = TrappingTypeOption.CLOTHING_OR_ACCESSORY.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    TrappingType.DrugOrPoison -> {
                        SimpleTrappingDetailBody(
                            subheadBar = subheadBar,
                            trappingType = TrappingTypeOption.DRUG_OR_POISON.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    TrappingType.FoodOrDrink -> {
                        SimpleTrappingDetailBody(
                            trappingType = TrappingTypeOption.FOOD_OR_DRINK.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    TrappingType.HerbOrDraught -> {
                        SimpleTrappingDetailBody(
                            subheadBar = subheadBar,
                            trappingType = TrappingTypeOption.HERB_OR_DRAUGHT.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    is TrappingType.MeleeWeapon -> {
                        MeleeWeaponDetailBody(
                            subheadBar = subheadBar,
                            damage = type.damage,
                            reach = type.reach,
                            group = type.group,
                            qualities = type.qualities,
                            flaws = type.flaws,
                            strengthBonus = null,
                            description = trapping.description,
                            encumbrance = trapping.encumbrance,
                            characterTrapping = null,
                        )
                    }

                    is TrappingType.RangedWeapon -> {
                        RangedWeaponDetailBody(
                            subheadBar = subheadBar,
                            damage = type.damage,
                            range = type.range,
                            group = type.group,
                            qualities = type.qualities,
                            flaws = type.flaws,
                            strengthBonus = null,
                            description = trapping.description,
                            encumbrance = trapping.encumbrance,
                            characterTrapping = null,
                        )
                    }

                    null -> {
                        SimpleTrappingDetailBody(
                            subheadBar = subheadBar,
                            trappingType = TrappingTypeOption.MISCELLANEOUS.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    TrappingType.SpellIngredient -> {
                        SimpleTrappingDetailBody(
                            subheadBar = subheadBar,
                            trappingType = TrappingTypeOption.SPELL_INGREDIENT.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    TrappingType.ToolOrKit -> {
                        SimpleTrappingDetailBody(
                            subheadBar = subheadBar,
                            trappingType = TrappingTypeOption.TOOL_OR_KIT.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }

                    TrappingType.TradeTools -> {
                        SimpleTrappingDetailBody(
                            subheadBar = subheadBar,
                            trappingType = TrappingTypeOption.TRADE_TOOLS.localizedName,
                            encumbrance = trapping.encumbrance,
                            description = trapping.description,
                            characterTrapping = null,
                        )
                    }
                }
            }
        ) { item, onDismissRequest ->
            TrappingDialog(
                existingTrapping = item,
                onDismissRequest = onDismissRequest,
                onSaveRequest = screenModel::update,
            )
        }
    }
}
