package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.TabPager
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.tab
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun CompendiumScreen(routing: Routing<Route.Compendium>) {
    Scaffold(topBar = { TopBar(routing) }) {
        val viewModel: CompendiumViewModel by viewModel { parametersOf(routing.route.partyId) }
        val strings = LocalStrings.current.compendium

        TabPager(Modifier.fillMaxSize()) {
            tab(strings.tabSkills) { SkillCompendiumTab(viewModel, screenWidth) }
            tab(strings.tabTalents) { TalentCompendiumTab(viewModel, screenWidth) }
            tab(strings.tabSpells) { SpellCompendiumTab(viewModel, screenWidth) }
            tab(strings.tabBlessings) { BlessingCompendiumTab(viewModel, screenWidth) }
            tab(strings.tabMiracles) { MiracleCompendiumTab(viewModel, screenWidth) }
        }
    }
}

@Composable
private fun TopBar(routing: Routing<Route.Compendium>) {
    val partyId = routing.route.partyId
    val viewModel: CompendiumViewModel by viewModel { parametersOf(partyId) }

    val strings = LocalStrings.current.compendium

    TopAppBar(
        title = {
            Column {
                Text(strings.title)
                viewModel.party.collectWithLifecycle(null).value?.let {
                    Subtitle(it.getName())
                }
            }
        },
        navigationIcon = { BackButton(onClick = { routing.pop() }) },
        actions = {
            TopBarAction(
                text = strings.buttonImport,
                onClick = { routing.navigateTo(Route.CompendiumImport(partyId)) },
            )
        }
    )
}

@Composable
fun <T : CompendiumItem<T>> CompendiumTab(
    liveItems: Flow<List<T>>,
    width: Dp,
    emptyUI: @Composable () -> Unit,
    dialog: @Composable (MutableState<DialogState<T?>>) -> Unit,
    remover: suspend (T) -> Unit,
    saver: suspend (T) -> Unit,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    val dialogState = remember { mutableStateOf<DialogState<T?>>(DialogState.Closed()) }

    dialog(dialogState)

    Scaffold(
        modifier = Modifier
            .width(width)
            .fillMaxHeight(),
        floatingActionButton = {
            FloatingActionButton(onClick = { dialogState.value = DialogState.Opened(null) }) {
                Icon(
                    Icons.Rounded.Add,
                    LocalStrings.current.compendium.iconAddCompendiumItem,
                )
            }
        }
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            val coroutineScope = rememberCoroutineScope { EmptyCoroutineContext + Dispatchers.IO }
            val items = liveItems.collectWithLifecycle(null).value

            when {
                items == null -> FullScreenProgress()
                items.isEmpty() -> emptyUI()
                else -> {
                    LazyColumn(contentPadding = PaddingValues(bottom = Spacing.bottomPaddingUnderFab)) {
                        items(items) { item ->
                            WithContextMenu(
                                items = listOf(
                                    ContextMenu.Item(
                                        LocalStrings.current.commonUi.buttonDuplicate,
                                        onClick = {
                                            coroutineScope.launch { saver(item.duplicate()) }
                                        }
                                    ),
                                    ContextMenu.Item(
                                        LocalStrings.current.commonUi.buttonRemove,
                                        onClick = { coroutineScope.launch { remover(item) } }
                                    ),
                                ),
                                onClick = { dialogState.value = DialogState.Opened(item) },
                            ) {
                                itemContent(item)
                            }
                        }
                    }
                }
            }
        }
    }
}
