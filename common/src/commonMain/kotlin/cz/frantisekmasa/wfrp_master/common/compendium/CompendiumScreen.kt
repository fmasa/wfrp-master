package cz.frantisekmasa.wfrp_master.common.compendium

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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.compendium.blessing.BlessingCompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.career.CareerCompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.miracle.MiracleCompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.skill.SkillCompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.spell.SpellCompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.talent.TalentCompendiumTab
import cz.frantisekmasa.wfrp_master.common.compendium.trait.TraitCompendiumTab
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogState
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.TabPager
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.tab
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class CompendiumScreen(
    private val partyId: PartyId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: CompendiumScreenModel = rememberScreenModel(arg = partyId)
        Scaffold(topBar = { TopBar(screenModel) }) {
            val strings = LocalStrings.current.compendium

            TabPager(Modifier.fillMaxSize()) {
                tab(strings.tabSkills) { SkillCompendiumTab(partyId, screenModel, screenWidth) }
                tab(strings.tabTalents) { TalentCompendiumTab(partyId, screenModel, screenWidth) }
                tab(strings.tabSpells) { SpellCompendiumTab(partyId, screenModel, screenWidth) }
                tab(strings.tabBlessings) { BlessingCompendiumTab(partyId, screenModel, screenWidth) }
                tab(strings.tabMiracles) { MiracleCompendiumTab(partyId, screenModel, screenWidth) }
                tab(strings.tabTraits) { TraitCompendiumTab(partyId, screenModel, screenWidth) }
                tab(strings.tabCareers) { CareerCompendiumTab(partyId, screenModel, screenWidth) }
            }
        }
    }

    @Composable
    private fun TopBar(screenModel: CompendiumScreenModel) {
        val strings = LocalStrings.current.compendium
        val navigator = LocalNavigator.currentOrThrow

        TopAppBar(
            title = {
                Column {
                    Text(strings.title)
                    screenModel.party.collectWithLifecycle(null).value?.let {
                        Subtitle(it.name)
                    }
                }
            },
            navigationIcon = { BackButton() },
            actions = {
                OptionsAction {
                    DropdownMenuItem(
                        onClick = { navigator.push(RulebookCompendiumImportScreen(partyId)) }
                    ) {
                        Text(strings.buttonImportFromRulebook)
                    }

                    DropdownMenuItem(
                        onClick = { navigator.push(JsonCompendiumImportScreen(partyId)) }
                    ) {
                        Text(strings.buttonImportFile)
                    }

                    DropdownMenuItem(
                        onClick = { navigator.push(JsonCompendiumExportScreen(partyId)) }
                    ) {
                        Text(strings.buttonExportFile)
                    }
                }
            }
        )
    }
}

@Composable
fun <T : CompendiumItem<T>> CompendiumTab(
    liveItems: Flow<List<T>>,
    width: Dp,
    emptyUI: @Composable () -> Unit,
    onClick: (T) -> Unit,
    remover: suspend (T) -> Unit,
    saver: suspend (T) -> Unit,
    onNewItemRequest: () -> Unit,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    Scaffold(
        modifier = Modifier
            .width(width)
            .fillMaxHeight(),
        floatingActionButton = {
            FloatingActionButton(onClick = onNewItemRequest) {
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
                        items(items, key = { it.id }) { item ->
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
                                onClick = { onClick(item) },
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

    CompendiumTab(
        liveItems = liveItems,
        width = width,
        emptyUI = emptyUI,
        onClick = { dialogState.value = DialogState.Opened(it) },
        onNewItemRequest = { dialogState.value = DialogState.Opened(null) },
        remover = remover,
        saver = saver,
        itemContent = itemContent,
    )
}
