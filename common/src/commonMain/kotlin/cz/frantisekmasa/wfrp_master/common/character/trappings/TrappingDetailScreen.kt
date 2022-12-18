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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

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
        val navigator = LocalNavigator.currentOrThrow

        if (trapping == null) {
            val message = LocalStrings.current.trappings.messages.trappingNotFound
            val snackbarHolder = LocalPersistentSnackbarHolder.current

            LaunchedEffect(Unit) {
                snackbarHolder.showSnackbar(message)
                navigator.pop()
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
            onOpenDetailRequest = { navigator.push(TrappingDetailScreen(characterId, it.id)) }
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
                        LocalStrings.current.character.titleEdit,
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
                    trappingType = LocalStrings.current.trappings.types.bookOrDocument,
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
                    trappingType = LocalStrings.current.trappings.types.drugOrPoison,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.FoodOrDrink -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = LocalStrings.current.trappings.types.foodOrDrink,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.HerbOrDraught -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = LocalStrings.current.trappings.types.herbOrDraught,
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
                    trappingType = LocalStrings.current.trappings.types.miscellaneous,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.SpellIngredient -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = LocalStrings.current.trappings.types.spellIngredient,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.ToolOrKit -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = LocalStrings.current.trappings.types.toolOrKit,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
            TrappingType.TradeTools -> {
                SimpleTrappingDetail(
                    trapping = trapping,
                    trappingType = LocalStrings.current.trappings.types.tradeTools,
                    onSaveRequest = screenModel::saveInventoryItem,
                )
            }
        }
    }
}
