package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.ChooseTrappingDialog
import cz.frantisekmasa.wfrp_master.common.character.trappings.InventoryItemDialog
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingItem
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.sum
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FloatingActionsMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.MenuState
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.core.utils.launchLogged
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers

@Composable
fun ContainerDetail(
    trapping: InventoryItem,
    container: TrappingType.Container,
    allItems: List<TrappingsScreenModel.Trapping>?,
    onSaveRequest: suspend (InventoryItem) -> Unit,
    onOpenDetailRequest: (InventoryItem) -> Unit,
    onRemoveRequest: suspend (InventoryItem) -> Unit,
    onRemoveFromContainerRequest: suspend (InventoryItem) -> Unit,
    onAddToContainerRequest: suspend (InventoryItem) -> Unit,
) {
    if (allItems == null) {
        FullScreenProgress()
        return
    }

    var newTrappingDialogOpened by rememberSaveable { mutableStateOf(false) }

    if (newTrappingDialogOpened) {
        InventoryItemDialog(
            onSaveRequest = { onSaveRequest(it) },
            existingItem = null,
            onDismissRequest = { newTrappingDialogOpened = false },
            defaultContainerId = trapping.id,
        )
    }

    var trappingPickerOpened by rememberSaveable { mutableStateOf(false) }

    if (trappingPickerOpened) {
        val trappingsToAdd by derivedStateOf {
            // Make sure we don't let user create cycles
            allItems.filter { it.item.id != trapping.id && it.item.id != trapping.containerId }
        }

        ChooseTrappingDialog(
            title = stringResource(Str.trappings_title_select_trapping),
            trappings = trappingsToAdd,
            onSelected = { onAddToContainerRequest(it.item) },
            emptyUiText = stringResource(Str.trappings_messages_no_trappings_to_add),
            onDismissRequest = { trappingPickerOpened = false },
        )
    }

    Scaffold(
        floatingActionButton = {
            if (trapping.containerId != null) {
                return@Scaffold
            }

            var menuState by remember { mutableStateOf(MenuState.COLLAPSED) }

            FloatingActionsMenu(
                state = menuState,
                onToggleRequest = { menuState = it },
                icon = rememberVectorPainter(Icons.Rounded.Add),
            ) {
                ExtendedFloatingActionButton(
                    icon = { Icon(rememberVectorPainter(Icons.Rounded.Search), null) },
                    text = { Text(stringResource(Str.trappings_button_select_existing)) },
                    onClick = {
                        menuState = MenuState.COLLAPSED
                        trappingPickerOpened = true
                    }
                )

                ExtendedFloatingActionButton(
                    icon = { Icon(rememberVectorPainter(Icons.Rounded.Add), null) },
                    text = { Text(stringResource(Str.trappings_button_create_new)) },
                    onClick = {
                        menuState = MenuState.COLLAPSED
                        newTrappingDialogOpened = true
                    }
                )
            }
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            WornBar(trapping, container, onSaveRequest)

            val carriedItems by derivedStateOf {
                allItems
                    .asSequence()
                    .flatMap { it.allItems }
                    .filter { it.containerId == trapping.id }
                    .toList()
            }

            Column(Modifier.padding(Spacing.bodyPadding)) {
                val currentlyCarries by derivedStateOf {
                    carriedItems.map { it.totalEncumbrance }.sum()
                }

                EncumbranceBox(trapping)

                SingleLineTextValue(
                    stringResource(Str.trappings_label_carries),
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                color = if (currentlyCarries > container.carries)
                                    MaterialTheme.colors.error
                                else Color.Unspecified
                            )
                        ) {
                            append(currentlyCarries.toString())
                        }

                        append("/")
                        append(container.carries.toString())
                    }
                )

                TrappingDescription(trapping)
            }

            if (trapping.containerId == null) {
                StoredTrappingsCard(
                    carriedItems,
                    onSaveRequest = onSaveRequest,
                    onOpenDetailRequest = onOpenDetailRequest,
                    onRemoveRequest = onRemoveRequest,
                    onRemoveFromContainerRequest = onRemoveFromContainerRequest,
                )
            } else {
                CardContainer(Modifier.padding(horizontal = Spacing.small)) {
                    Text(
                        stringResource(Str.trappings_messages_cannot_store_trappings_in_container_stored_in_container),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(horizontal = Spacing.small)
                    )
                }
            }
        }
    }
}

@Composable
private fun StoredTrappingsCard(
    trappings: List<InventoryItem>,
    onSaveRequest: suspend (InventoryItem) -> Unit,
    onRemoveFromContainerRequest: suspend (InventoryItem) -> Unit,
    onOpenDetailRequest: (InventoryItem) -> Unit,
    onRemoveRequest: suspend (InventoryItem) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    CardContainer(Modifier.padding(horizontal = Spacing.small)) {
        Column(Modifier.padding(horizontal = Spacing.small)) {
            CardTitle(stringResource(Str.trappings_title_stored_trappings))

            if (trappings.isEmpty()) {
                EmptyUI(
                    text = stringResource(Str.trappings_messages_no_items),
                    Resources.Drawable.TrappingContainer,
                    size = EmptyUI.Size.Small
                )
            } else {
                trappings.forEachIndexed { index, trapping ->
                    key(trapping.id) {
                        Column {
                            TrappingItem(
                                trapping = TrappingsScreenModel.Trapping.SeparateTrapping(trapping),
                                onClick = { onOpenDetailRequest(trapping) },
                                onDuplicate = {
                                    coroutineScope.launchLogged(Dispatchers.IO) {
                                        onSaveRequest(trapping.duplicate())
                                    }
                                },
                                onRemove = {
                                    coroutineScope.launchLogged(Dispatchers.IO) {
                                        onRemoveRequest(trapping)
                                    }
                                },
                                additionalContextItems = listOf(
                                    ContextMenu.Item(stringResource(Str.trappings_button_take_out)) {
                                        coroutineScope.launchLogged(Dispatchers.IO) {
                                            onRemoveFromContainerRequest(trapping)
                                        }
                                    }
                                )
                            )

                            if (index != trappings.lastIndex) {
                                Divider()
                            }
                        }
                    }
                }
            }
        }
    }
}
