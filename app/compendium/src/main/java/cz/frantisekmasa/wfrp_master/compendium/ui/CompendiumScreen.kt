package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.layout.WithConstraintsScope
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.Dp
import cz.frantisekmasa.wfrp_master.compendium.R
import cz.frantisekmasa.wfrp_master.compendium.domain.CompendiumItem
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabContent
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabRow
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabScreen
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.rememberPagerState
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun CompendiumScreen(routing: Routing<Route.Compendium>) {
    Scaffold(topBar = { TopBar(routing) }) {
        WithConstraints(Modifier.fillMaxSize()) {
            MainContent(routing)
        }
    }
}

@Composable
private fun TopBar(routing: Routing<Route.Compendium>) {
    val partyId = routing.route.partyId
    val viewModel: CompendiumViewModel by viewModel { parametersOf(partyId) }

    TopAppBar(
        title = {
            Column {
                Text(stringResource(R.string.title_compendium))
                viewModel.party.collectAsState(null).value?.let {
                    Subtitle(it.getName())
                }
            }
        },
        navigationIcon = { BackButton(onClick = { routing.pop() }) },
        actions = {
            TopBarAction(onClick = { routing.navigateTo(Route.CompendiumImport(partyId)) }) {
                Text(stringResource(R.string.button_import).toUpperCase(Locale.current))
            }
        }
    )
}

@Composable
private fun WithConstraintsScope.MainContent(routing: Routing<Route.Compendium>) {
    val screenWidth = constraints.maxWidth.toFloat()

    Column {
        val viewModel: CompendiumViewModel by viewModel { parametersOf(routing.route.partyId) }

        val tabs = tabs(routing.route.partyId)
        val pagerState = rememberPagerState(screenWidth, tabs.size)

        TabRow(
            tabs,
            pagerState = pagerState,
            fullWidthTabs = true,
        )

        TabContent(
            item = viewModel,
            screens = tabs,
            state = pagerState,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun WithConstraintsScope.tabs(partyId: PartyId): Array<TabScreen<CompendiumViewModel>> {
    val viewModel: CompendiumViewModel by viewModel { parametersOf(partyId) }

    return arrayOf(
        TabScreen(R.string.tab_skills) { SkillCompendiumTab(viewModel) },
        TabScreen(R.string.tab_talents) { TalentCompendiumTab(viewModel) },
        TabScreen(R.string.tab_spells) { SpellCompendiumTab(viewModel) }
    )
}

@Composable
fun <T : CompendiumItem> CompendiumTab(
    liveItems: StateFlow<List<T>?>,
    width: Dp,
    emptyUI: @Composable () -> Unit,
    dialog: @Composable (MutableState<DialogState<T?>>) -> Unit,
    onRemove: (T) -> Unit,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    val dialogState = remember { mutableStateOf<DialogState<T?>>(DialogState.Closed()) }

    dialog(dialogState)

    Scaffold(
        modifier = Modifier.width(width).fillMaxHeight(),
        floatingActionButton = {
            FloatingActionButton(onClick = { dialogState.value = DialogState.Opened(null) }) {
                Icon(vectorResource(R.drawable.ic_add))
            }
        }
    ) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            val items = liveItems.collectAsState().value

            when {
                items == null -> FullScreenProgress()
                items.isEmpty() -> emptyUI()
                else -> {
                    val contextMenuOpened = remember { mutableStateOf<UUID?>(null) }

                    LazyColumnFor(
                        items = items,
                    ) { item ->
                        Box(
                            Modifier
                                .clickable(
                                    onClick = { dialogState.value = DialogState.Opened(item) }
                                )
                                .longPressGestureFilter { contextMenuOpened.value = item.id }
                        ) {
                            itemContent(item)

                            ContextMenu(
                                items = listOf(
                                    ContextMenu.Item(
                                        stringResource(R.string.button_remove),
                                        onClick = { onRemove(item) })
                                ),
                                onDismissRequest = { contextMenuOpened.value = null },
                                expanded = contextMenuOpened.value == item.id
                            )
                        }
                    }
                }
            }
        }
    }
}
