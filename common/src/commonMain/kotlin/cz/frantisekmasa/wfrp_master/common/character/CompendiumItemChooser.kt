package cz.frantisekmasa.wfrp_master.common.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
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
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(title) },
            )
        }
    ) {
        val compendiumItems =
            screenModel.notUsedItemsFromCompendium.collectWithLifecycle(null).value
        val totalCompendiumItemCount =
            screenModel.compendiumItemsCount.collectWithLifecycle(null).value
        var saving by remember { mutableStateOf(false) }

        if (compendiumItems == null || totalCompendiumItemCount == null || saving) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumItems.isEmpty()) {
                    EmptyUI(
                        icon = emptyUiIcon,
                        text = LocalStrings.current.compendium.messages.noItems,
                        subText = if (totalCompendiumItemCount == 0)
                            LocalStrings.current.compendium.messages.noItemsInCompendiumSubtextPlayer
                        else null,
                    )
                } else {
                    val coroutineScope = rememberCoroutineScope()

                    LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
                        items(compendiumItems, key = { it.id }) { item ->
                            ListItem(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        saving = false

                                        coroutineScope.launch(Dispatchers.IO) { onSelect(item) }
                                    }
                                ),
                                icon = { ItemIcon(icon(item), ItemIcon.Size.Small) },
                                text = { Text(item.name) }
                            )
                        }
                    }
                }
            }

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
