package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.StickyHeader
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CompactEmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

fun <T> LazyListScope.characterItemsCard(
    title: @Composable () -> String,
    key: String,
    id: (T) -> Any,
    items: ImmutableList<T>,
    actions: (@Composable () -> Unit)? = null,
    newItemScreen: () -> Screen,
    detailScreen: (T) -> Screen,
    contextMenuItems: @Composable (T) -> List<ContextMenu.Item> = { emptyList() },
    leadingDivider: Boolean = false,
    onRemove: (T) -> Unit,
    noItems: @Composable () -> Unit = {
        CompactEmptyUI(stringResource(Str.common_ui_messages_no_items))
    },
    item: @Composable (T) -> Unit,
) {
    stickyHeader(key = "$key-header") {
        StickyHeader {
            if (leadingDivider) {
                Divider()
            }

            CardTitle(
                title(),
                actions = {
                    val navigation = LocalNavigationTransaction.current

                    IconButton(
                        onClick = { navigation.navigate(newItemScreen()) },
                    ) {
                        Icon(Icons.Rounded.Add, stringResource(Str.common_ui_button_add_item))
                    }

                    actions?.invoke()
                },
            )
        }
    }

    if (items.isEmpty()) {
        item(key = "$key-empty-ui") {
            if (items.isEmpty()) {
                noItems()
            }
        }
    }

    itemsIndexed(items, key = { _, it -> key to id(it) }) { index, it ->
        Column {
            if (index != 0) {
                Divider()
            }

            val navigation = LocalNavigationTransaction.current
            WithContextMenu(
                items =
                    contextMenuItems(it) +
                        ContextMenu.Item(
                            stringResource(Str.common_ui_button_remove),
                            onClick = { onRemove(it) },
                        ),
                onClick = { navigation.navigate(detailScreen(it)) },
            ) {
                item(it)
            }
        }
    }
}
