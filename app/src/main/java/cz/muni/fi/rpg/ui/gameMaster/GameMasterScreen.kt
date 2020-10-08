package cz.muni.fi.rpg.ui.gameMaster

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.github.zsoltk.compose.router.BackStack
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.AdManager
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.gameMaster.calendar.ChangeDateDialog
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncounterDialog
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncountersScreen
import cz.muni.fi.rpg.ui.router.Route
import cz.muni.fi.rpg.ui.router.Routing
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import cz.muni.fi.rpg.viewModels.GameMasterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.core.parameter.parametersOf

@ExperimentalCoroutinesApi
@Composable
fun GameMasterScreen(routing: Routing<Route.GameMaster>, adManager: AdManager) {
    val viewModel: GameMasterViewModel by viewModel { parametersOf(routing.route.partyId) }
    val party = viewModel.party.right().observeAsState().value
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
            Box(Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
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

@ExperimentalCoroutinesApi
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
                onChangeTimeRequest = {
                    val time = party.getTime().time
                    // TODO: Implement this
//                TimePickerDialog.newInstance(
//                    this@GameMasterFragment,
//                    time.hour,
//                    time.minute,
//                    true
//                ).show(childFragmentManager, "TimePickerDialog")
                },
                onChangeDateRequest = {
                    ChangeDateDialog.newInstance(party.id, party.getTime().date)
                        .show(fragmentManager, null)
                },
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