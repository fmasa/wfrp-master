package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.AmmunitionDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.ArmourDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.ContainerDetail
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.EquipBar
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.MeleeWeaponDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.QuantityBar
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.RangedWeaponDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.SimpleTrappingDetailBody
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.WornBar
import cz.frantisekmasa.wfrp_master.common.compendium.trapping.CompendiumTrappingDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItemId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import dev.icerock.moko.resources.compose.stringResource

class TrappingDetailScreen(
    characterId: CharacterId,
    itemId: InventoryItemId,
) : CharacterItemDetailScreen(characterId, itemId) {
    override val key =
        "parties/${characterId.partyId}/characters/${characterId.id}/trappings/$itemId"

    @Composable
    override fun Content() {
        val screenModel: TrappingsScreenModel = rememberScreenModel(arg = characterId)

        Detail(screenModel) { trapping, isGameMaster ->
            val (dialogOpened, setDialogOpened) = rememberSaveable { mutableStateOf(false) }

            if (dialogOpened) {
                EditTrappingDialog(
                    onSaveRequest = screenModel::saveItem,
                    existingItem = trapping,
                    onDismissRequest = { setDialogOpened(false) },
                )
            }

            val navigation = LocalNavigationTransaction.current
            TrappingDetail(
                trapping = trapping,
                screenModel = screenModel,
                isGameMaster = isGameMaster,
                onEditRequest = { setDialogOpened(true) },
                onOpenDetailRequest = {
                    navigation.navigate(TrappingDetailScreen(characterId, it.id))
                },
            )
        }
    }
}

@Composable
private fun TrappingDetail(
    trapping: InventoryItem,
    screenModel: TrappingsScreenModel,
    isGameMaster: Boolean,
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
        val subheadBar: @Composable ColumnScope.() -> Unit = @Composable {
            when (val type = trapping.trappingType) {
                is TrappingType.WearableTrapping -> {
                    WornBar(trapping, type, onChange = screenModel::saveItem)
                }

                is TrappingType.Weapon -> {
                    EquipBar(trapping, type, onChange = screenModel::saveItem)

                    if (
                        type is TrappingType.RangedWeapon &&
                        type.group == RangedWeaponGroup.THROWING
                    ) {
                        QuantityBar(trapping, onChange = screenModel::saveItem)
                    }
                }

                else -> {
                    QuantityBar(trapping, onChange = screenModel::saveItem)
                }
            }

            if (isGameMaster && trapping.compendiumId != null) {
                val navigation = LocalNavigationTransaction.current

                CompendiumButton(
                    modifier = Modifier
                        .padding(top = Spacing.bodyPadding)
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        navigation.navigate(
                            CompendiumTrappingDetailScreen(
                                screenModel.characterId.partyId,
                                trapping.compendiumId,
                            )
                        )
                    }
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
                    characterTrapping = trapping,
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
                    characterTrapping = trapping,
                )
            }
            is TrappingType.SimpleTrapping -> {
                SimpleTrappingDetailBody(
                    subheadBar = subheadBar,
                    trappingType = stringResource(type.name),
                    encumbrance = trapping.encumbrance,
                    description = trapping.description,
                    characterTrapping = trapping,
                )
            }
            null -> {
                SimpleTrappingDetailBody(
                    subheadBar = subheadBar,
                    trappingType = TrappingTypeOption.MISCELLANEOUS.localizedName,
                    encumbrance = trapping.encumbrance,
                    description = trapping.description,
                    characterTrapping = trapping,
                )
            }
            is TrappingType.Container -> {
                ContainerDetail(
                    subheadBar = subheadBar,
                    trapping = trapping,
                    container = type,
                    allItems = screenModel.inventory.collectWithLifecycle(null).value,
                    screenModel = screenModel,
                    onSaveRequest = screenModel::saveItem,
                    onOpenDetailRequest = onOpenDetailRequest,
                    onRemoveRequest = screenModel::removeItem,
                    onRemoveFromContainerRequest = screenModel::removeFromContainer,
                    onAddToContainerRequest = {
                        screenModel.addToContainer(trapping = it, container = trapping)
                    }
                )
            }
            is TrappingType.ClothingOrAccessory -> {
                SimpleTrappingDetailBody(
                    subheadBar = subheadBar,
                    trappingType = TrappingTypeOption.CLOTHING_OR_ACCESSORY.localizedName,
                    encumbrance = trapping.encumbrance,
                    description = trapping.description,
                    characterTrapping = trapping,
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
                    strengthBonus = screenModel.strengthBonus.collectWithLifecycle(null).value,
                    description = trapping.description,
                    encumbrance = trapping.encumbrance,
                    characterTrapping = trapping,
                )
            }
            is TrappingType.Prosthetic -> {
                SimpleTrappingDetailBody(
                    subheadBar = subheadBar,
                    trappingType = TrappingTypeOption.PROSTHETIC.localizedName,
                    encumbrance = trapping.encumbrance,
                    description = trapping.description,
                    characterTrapping = trapping,
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
                    strengthBonus = screenModel.strengthBonus.collectWithLifecycle(null).value,
                    description = trapping.description,
                    encumbrance = trapping.encumbrance,
                    characterTrapping = trapping,
                )
            }
        }
    }
}
