package cz.muni.fi.rpg.ui.gameMaster

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.github.zsoltk.compose.router.BackStack
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabContent
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabRow
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabScreen
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.fragmentManager
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncounterDialog
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncountersScreen
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GameMasterScreen(routing: Routing<Route.GameMaster>, adManager: AdManager) {
    val viewModel = ViewModel.GameMaster(routing.route.partyId)
    val party = viewModel.party.collectAsState(null).value
    val fragmentManager = fragmentManager()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { party?.let { Text(it.getName()) } },
                navigationIcon = { HamburgerButton() },
                actions = {
                    TopBarAction(
                        onClick = {
                            if (party == null) {
                                return@TopBarAction
                            }

                            RenamePartyDialog.newInstance(party.id, party.getName())
                                .show(fragmentManager, null)
                        },
                    ) {
                        Icon(vectorResource(R.drawable.ic_edit))
                    }
                }
            )
        }
    ) {
        if (party == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            return@Scaffold
        }

        WithConstraints(Modifier.fillMaxSize()) {
            val screens = screens(
                viewModel,
                routing.backStack,
                Modifier.width(maxWidth).padding(top = 6.dp)
            )
            val screenWidth = constraints.maxWidth.toFloat()

            Column(Modifier.fillMaxHeight()) {
                val scrollState = key(screenWidth, screens.size) { rememberScrollState(0f) }

                TabRow(
                    screens,
                    scrollState = scrollState,
                    screenWidth = screenWidth,
                    fullWidthTabs = true,
                )

                TabContent(
                    item = party,
                    screens = screens,
                    scrollState = scrollState,
                    screenWidth = screenWidth,
                    modifier = Modifier.weight(1f)
                )

                BannerAd(
                    unitId = stringResource(R.string.game_master_ad_unit_id),
                    adManager = adManager
                )
            }
        }
    }
}

@Composable
private fun screens(
    viewModel: GameMasterViewModel,
    backStack: BackStack<Route>,
    modifier: Modifier
): Array<TabScreen<Party>> {
    val fragmentManager = fragmentManager()

    return arrayOf(
        TabScreen(R.string.title_characters) { party ->
            val dialogTitle = stringResource(R.string.title_party_ambitions)
            PartySummaryScreen(
                modifier = modifier,
                partyId = party.id,
                viewModel = viewModel,
                backStack = backStack,
                onCharacterOpenRequest = {
                    backStack.push(Route.CharacterDetail(CharacterId(party.id, it.id)))
                },
                onCharacterCreateRequest = {
                    backStack.push(Route.CharacterCreation(party.id, it))
                },
                onEditAmbitionsRequest = { ambitions ->
                    ChangeAmbitionsDialog
                        .newInstance(dialogTitle, ambitions)
                        .setOnSaveListener { viewModel.updatePartyAmbitions(it) }
                        .show(fragmentManager, null)
                }
            )
        },
        TabScreen(R.string.title_calendar) { party ->
            CalendarScreen(
                party,
                modifier = modifier,
                viewModel = viewModel,
            )
        },
        TabScreen(R.string.title_encounters) { party ->
            val encountersViewModel: EncountersViewModel by viewModel { parametersOf(party.id) }
            EncountersScreen(
                encountersViewModel,
                modifier = modifier,
                onEncounterClick = {
                    backStack.push(Route.EncounterDetail(EncounterId(party.id, it.id)))
                },
                onNewEncounterDialogRequest = {
                    EncounterDialog.newInstance(party.id, null)
                        .show(fragmentManager, null)
                }
            )
        },
    )
}