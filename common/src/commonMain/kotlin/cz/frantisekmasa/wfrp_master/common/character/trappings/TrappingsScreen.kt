package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingsScreenModel.Trapping
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CardButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTip
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTipCard
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopPanel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun TrappingsScreen(
    screenModel: TrappingsScreenModel,
    characterId: CharacterId,
    modifier: Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        TopPanel {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                CharacterEncumbrance(
                    screenModel,
                    Modifier.padding(Spacing.medium),
                )

                screenModel.money.collectWithLifecycle(null).value?.let { money ->
                    var transactionDialogVisible by rememberSaveable { mutableStateOf(false) }

                    MoneyBalance(
                        money,
                        Modifier
                            .clickable { transactionDialogVisible = true }
                            .padding(Spacing.medium)
                            .padding(end = 8.dp),
                    )

                    if (transactionDialogVisible) {
                        TransactionDialog(
                            money,
                            screenModel,
                            onDismissRequest = { transactionDialogVisible = false },
                        )
                    }
                }
            }
        }

        UserTipCard(UserTip.ARMOUR_TRAPPINGS, Modifier.padding(horizontal = 8.dp))

        var newTrappingDialogOpened by rememberSaveable { mutableStateOf(false) }

        if (newTrappingDialogOpened) {
            InventoryItemDialog(
                onSaveRequest = screenModel::saveInventoryItem,
                defaultContainerId = null,
                existingItem = null,
                onDismissRequest = { newTrappingDialogOpened = false }
            )
        }

        val trappings = screenModel.inventory.collectWithLifecycle(null).value
            ?: return@Column

        var addToContainerDialogTrapping: InventoryItem?
            by remember { mutableStateOf(null) }

        addToContainerDialogTrapping?.let { trapping ->
            val containers by derivedStateOf {
                trappings.filter { it.item.id != trapping.id && it is Trapping.Container }
            }

            ChooseTrappingDialog(
                title = LocalStrings.current.trappings.titleSelectContainer,
                trappings = containers,
                onSelected = { screenModel.addToContainer(trapping, it.item) },
                emptyUiText = LocalStrings.current.trappings.messages.noContainersFound,
                onDismissRequest = { addToContainerDialogTrapping = null },
            )
        }

        val coroutineScope = rememberCoroutineScope { EmptyCoroutineContext + Dispatchers.IO }
        val navigation = LocalNavigationTransaction.current

        InventoryItemsCard(
            trappings = trappings,
            onClick = { navigation.navigate(TrappingDetailScreen(characterId, it.id)) },
            onRemove = { screenModel.removeInventoryItem(it) },
            onDuplicate = { coroutineScope.launch { screenModel.saveInventoryItem(it.duplicate()) } },
            onNewItemButtonClicked = { newTrappingDialogOpened = true },
            onAddToContainerRequest = { addToContainerDialogTrapping = it },
        )

        Spacer(Modifier.padding(bottom = 20.dp))
    }
}

@Composable
private fun CharacterEncumbrance(screenModel: TrappingsScreenModel, modifier: Modifier) {
    val max = screenModel.maxEncumbrance.collectWithLifecycle(null).value
    val total = screenModel.totalEncumbrance.collectWithLifecycle(null).value

    val isOverburdened = max != null && total != null && total > max

    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            drawableResource(Resources.Drawable.TrappingEncumbrance),
            LocalStrings.current.trappings.iconTotalEncumbrance,
            Modifier.size(18.dp),
        )
        Text(
            "${total ?: "?"} / ${max ?: "?"}",
            color = if (isOverburdened) MaterialTheme.colors.error else LocalContentColor.current
        )
    }
}

@Composable
private fun InventoryItemsCard(
    trappings: List<Trapping>,
    onClick: (InventoryItem) -> Unit,
    onRemove: (InventoryItem) -> Unit,
    onDuplicate: (InventoryItem) -> Unit,
    onNewItemButtonClicked: () -> Unit,
    onAddToContainerRequest: (InventoryItem) -> Unit,
) {
    val strings = LocalStrings.current.trappings

    CardContainer(Modifier.padding(horizontal = 8.dp)) {
        Column(Modifier.padding(horizontal = 8.dp)) {
            CardTitle(strings.title)
            if (trappings.isEmpty()) {
                EmptyUI(
                    text = strings.messages.noItems,
                    Resources.Drawable.TrappingContainer,
                    size = EmptyUI.Size.Small
                )
            } else {
                InventoryItemList(
                    trappings,
                    onClick = onClick,
                    onRemove = onRemove,
                    onDuplicate = onDuplicate,
                    onAddToContainerRequest = onAddToContainerRequest,
                )
            }

            CardButton(strings.titleAdd, onClick = onNewItemButtonClicked)
        }
    }
}
