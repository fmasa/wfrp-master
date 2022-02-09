package cz.frantisekmasa.wfrp_master.religion.ui.miracles

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
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun CompendiumMiracleChooser(
    viewModel: MiraclesViewModel,
    onComplete: () -> Unit,
    onCustomMiracleRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val strings = LocalStrings.current.miracles

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(strings.titleChooseCompendiumSpell) },
            )
        }
    ) {
        val compendiumMiracles = viewModel.notUsedItemsFromCompendium.collectWithLifecycle(null).value
        val totalCompendiumMiracleCount = viewModel.compendiumItemsCount.collectWithLifecycle(null).value
        var saving by remember { mutableStateOf(false) }

        if (compendiumMiracles == null || totalCompendiumMiracleCount == null || saving) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumMiracles.isEmpty()) {
                    EmptyUI(
                        icon = Resources.Drawable.Miracle,
                        text = strings.messages.noMiraclesInCompendium,
                        subText = when (totalCompendiumMiracleCount) {
                            0 -> strings.messages.noMiraclesInCompendiumSubtextPlayer
                            else -> null
                        },
                    )
                } else {
                    val coroutineScope = rememberCoroutineScope()

                    LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
                        items(compendiumMiracles) { miracle ->
                            ListItem(
                                modifier = Modifier.clickable(
                                    onClick = {
                                        saving = false

                                        coroutineScope.launch(Dispatchers.IO) {
                                            viewModel.saveItem(
                                                Miracle.fromCompendium(miracle)
                                            )

                                            withContext(Dispatchers.Main) { onComplete() }
                                        }
                                    }
                                ),
                                icon = { ItemIcon(Resources.Drawable.Spell, ItemIcon.Size.Small) },
                                text = { Text(miracle.name) }
                            )
                        }
                    }
                }
            }

            OutlinedButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.bodyPadding),
                onClick = onCustomMiracleRequest,
            ) {
                Text(strings.buttonAddNonCompendium)
            }
        }
    }
}
