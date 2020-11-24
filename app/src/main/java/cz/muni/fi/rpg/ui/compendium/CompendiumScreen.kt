package cz.muni.fi.rpg.ui.compendium

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.WithConstraintsScope
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.Dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.compendium.CompendiumItem
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.composables.dialog.DialogState
import cz.muni.fi.rpg.ui.router.Route
import cz.muni.fi.rpg.ui.router.Routing
import cz.muni.fi.rpg.viewModels.CompendiumViewModel
import cz.muni.fi.rpg.viewModels.PartyViewModel
import kotlinx.coroutines.flow.Flow
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
    val viewModel: PartyViewModel by viewModel { parametersOf(partyId) }

    TopAppBar(
        title = {
            Column {
                Text(stringResource(R.string.title_compendium))
                viewModel.party.right().collectAsState(null).value?.let {
                    Subtitle(it.getName())
                }
            }
        },
        navigationIcon = { BackButton(onClick = { routing.backStack.pop() }) },
        actions = {
            TopBarAction(onClick = { routing.backStack.push(Route.CompendiumImport(partyId)) }) {
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
        val scrollState = key(screenWidth, tabs.size) { rememberScrollState(0f) }

        TabRow(
            tabs,
            scrollState = scrollState,
            screenWidth = screenWidth,
            fullWidthTabs = true,
        )

        TabContent(
            item = viewModel,
            screens = tabs,
            scrollState = scrollState,
            screenWidth = screenWidth,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WithConstraintsScope.tabs(partyId: UUID): Array<TabScreen<CompendiumViewModel>> {
    val viewModel: CompendiumViewModel by viewModel { parametersOf(partyId) }

    return arrayOf(
        TabScreen(R.string.title_character_skills) { SkillCompendiumTab(viewModel) },
        TabScreen(R.string.title_character_talents) { TalentCompendiumTab(viewModel) },
        TabScreen(R.string.title_character_spells) { SpellCompendiumTab(viewModel) }
    )
}

@Composable
fun <T : CompendiumItem> CompendiumTab(
    liveItems: Flow<List<T>>,
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
            val items = liveItems.collectAsState(null).value

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
                                        stringResource(R.string.remove),
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
