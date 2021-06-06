package cz.frantisekmasa.wfrp_master.religion.ui.miracles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.religion.R
import cz.frantisekmasa.wfrp_master.religion.domain.Miracle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


@Composable
internal fun CompendiumMiracleChooser(
    viewModel: MiraclesViewModel,
    onComplete: () -> Unit,
    onCustomMiracleRequest: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(stringResource(R.string.title_choose_compendium_miracle)) },
            )
        }
    ) {
        val compendiumMiracles = viewModel.notUsedItemsFromCompendium.observeAsState().value
        val totalCompendiumMiracleCount = viewModel.compendiumItemsCount.observeAsState().value
        var saving by remember { mutableStateOf(false) }

        if (compendiumMiracles == null || totalCompendiumMiracleCount == null || saving) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f)) {
                if (compendiumMiracles.isEmpty()) {
                    EmptyUI(
                        drawableResourceId = R.drawable.ic_pray,
                        textId = R.string.no_miracles_in_compendium,
                        subTextId = if (totalCompendiumMiracleCount == 0)
                            R.string.no_items_in_compendium_sub_text_player else
                            null,
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
                                icon = { ItemIcon(R.drawable.ic_spells, ItemIcon.Size.Small) },
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
                Text(stringResource(R.string.button_add_non_compendium_miracle))
            }
        }
    }
}