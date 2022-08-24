package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.SearchableList
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
internal fun <A : CharacterItem, B : CompendiumItem<B>> CompendiumItemChooser(
    title: String,
    icon: @Composable (B) -> Resources.Drawable,
    screenModel: CharacterItemScreenModel<A, B>,
    onSelect: suspend (B) -> Unit,
    onCustomItemRequest: () -> Unit,
    onDismissRequest: () -> Unit,
    customItemButtonText: String,
    emptyUiIcon: Resources.Drawable,
) {
    val compendiumItems =
        screenModel.notUsedItemsFromCompendium.collectWithLifecycle(null).value
    val totalCompendiumItemCount =
        screenModel.compendiumItemsCount.collectWithLifecycle(null).value

    val data by derivedStateOf {
        if (compendiumItems == null || totalCompendiumItemCount == null)
            SearchableList.Data.Loading
        else SearchableList.Data.Loaded(compendiumItems)
    }

    val coroutineScope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        SearchableList(
            modifier = Modifier.weight(1f),
            data = data,
            navigationIcon = { CloseButton(onDismissRequest) },
            title = title,
            emptyUi = {
                EmptyUI(
                    icon = emptyUiIcon,
                    text = LocalStrings.current.compendium.messages.noItems,
                    subText = if (totalCompendiumItemCount == 0)
                        LocalStrings.current.compendium.messages.noItemsInCompendiumSubtextPlayer
                    else null,
                )
            },
            searchPlaceholder = LocalStrings.current.compendium.searchPlaceholder,
            key = { it.id },
            searchableValue = { it.name },
        ) { item ->
            ListItem(
                modifier = Modifier.clickable(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) { onSelect(item) }
                    }
                ),
                icon = { ItemIcon(icon(item), ItemIcon.Size.Small) },
                text = { Text(item.name) }
            )
        }

        Surface(elevation = 8.dp) {
            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.bodyPadding),
                onClick = onCustomItemRequest,
            ) {
                Text(customItemButtonText)
            }
        }
    }
}
