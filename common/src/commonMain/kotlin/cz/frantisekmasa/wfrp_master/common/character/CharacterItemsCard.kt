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
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.CompactEmptyUI
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

fun <T> LazyListScope.characterItemsCard(
    title: @Composable () -> String,
    key: String,
    id: (T) -> Any,
    items: ImmutableList<T>,
    actions: (@Composable () -> Unit)? = null,
    newItemScreen: () -> Screen,
    item: @Composable (T) -> Unit,
) {
    stickyHeader(key = "$key-header") {
        StickyHeader {
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
                CompactEmptyUI(stringResource(Str.common_ui_messages_no_items))
            }
        }
    }

    itemsIndexed(items, key = { _, it -> key to id(it) }) { index, it ->
        Column {
            if (index != 0) {
                Divider()
            }

            item(it)
        }
    }
}
