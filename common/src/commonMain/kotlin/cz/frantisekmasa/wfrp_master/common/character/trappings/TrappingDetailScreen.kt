package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.AmmunitionDetail
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.ArmourDetail
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.ClothingOrAccessoryDetail
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.ContainerDetail
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.MeleeWeaponDetail
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.RangedWeaponDetail
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.SimpleTrappingDetail
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource

data class TrappingDetailScreen(
    private val characterId: CharacterId,
    private val trappingId: InventoryItemId
) : Screen {
    override val key =
        "parties/${characterId.partyId}/characters/${characterId.id}/trappings/$trappingId"

    @Composable
    override fun Content() {
        val screenModel: TrappingsScreenModel = rememberScreenModel(arg = characterId)
        val trappings = screenModel.items.collectWithLifecycle(null).value

        if (trappings == null) {
            FullScreenProgress()
            return
        }

        val trapping = remember(trappings) { trappings.firstOrNull { it.id == trappingId } }
        val navigation = LocalNavigationTransaction.current

        if (trapping == null) {
            val message = stringResource(Str.trappings_messages_trapping_not_found)
            val snackbarHolder = LocalPersistentSnackbarHolder.current

            LaunchedEffect(Unit) {
                snackbarHolder.showSnackbar(message)
                navigation.goBack()
            }

            return
        }

        val (dialogOpened, setDialogOpened) = rememberSaveable { mutableStateOf(false) }

        if (dialogOpened) {
            InventoryItemDialog(
                onSaveRequest = screenModel::saveInventoryItem,
                defaultContainerId = null,
                existingItem = trapping,
                onDismissRequest = { setDialogOpened(false) },
            )
        }

        TrappingDetail(
            trapping,
            screenModel,
            onEditRequest = { setDialogOpened(true) },
            onOpenDetailRequest = { navigation.navigate(TrappingDetailScreen(characterId, it.id)) },
        )
    }
}

@Composable
private fun TrappingDetail(
    trapping: InventoryItem,
    screenModel: TrappingsScreenModel,
    onEditRequest: () -> Unit,
    onOpenDetailRequest: (InventoryItem) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(trapping.name) },
                navigationIcon = { BackButton() },
                actions = {
                    IconAction(
                        Icons.Rounded.Edit,
                        stringResource(Str.character_title_edit),
                        onClick = onEditRequest,
                    )
                }
            )
        },
    ) {
        when (val type = trapping.trappingType) {
            is TrappingType.Ammunition -> {
                AmmunitionDetail(
                    trapping = trapping,
                    ammunition = type,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            is TrappingType.Armour -> {
                ArmourDetail(
                    trapping = trapping,
                    armour = type,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.BookOrDocument -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = stringResource(Str.trappings_types_book_or_document),
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            is TrappingType.Container -> {
                ContainerDetail(
                    trapping = trapping,
                    container = type,
                    allItems = screenModel.inventory.collectWithLifecycle(null).value,
                    onSaveRequest = screenModel::saveInventoryItem,
                    onOpenDetailRequest = onOpenDetailRequest,
                    onRemoveRequest = screenModel::removeInventoryItem,
                    onRemoveFromContainerRequest = screenModel::removeFromContainer,
                    onAddToContainerRequest = {
                        screenModel.addToContainer(trapping = it, container = trapping)
                    }
                )
            }
            is TrappingType.ClothingOrAccessory -> {
                ClothingOrAccessoryDetail(
                    trapping = trapping,
                    clothingOrAccessory = type,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.DrugOrPoison -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = stringResource(Str.trappings_types_drug_or_poison),
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.FoodOrDrink -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = stringResource(Str.trappings_types_food_or_drink),
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.HerbOrDraught -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = stringResource(Str.trappings_types_herb_or_draught),
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            is TrappingType.MeleeWeapon -> {
                MeleeWeaponDetail(
                    trapping = trapping,
                    meleeWeapon = type,
                    strengthBonus = screenModel.strengthBonus.collectWithLifecycle(null).value,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            is TrappingType.RangedWeapon -> {
                RangedWeaponDetail(
                    trapping = trapping,
                    rangedWeapon = type,
                    strengthBonus = screenModel.strengthBonus.collectWithLifecycle(null).value,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            null -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = stringResource(Str.trappings_types_miscellaneous),
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.SpellIngredient -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = stringResource(Str.trappings_types_spell_ingredient),
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.ToolOrKit -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = stringResource(Str.trappings_types_tool_or_kit),
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.TradeTools -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = stringResource(Str.trappings_types_trade_tools),
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
        }
    }
}
