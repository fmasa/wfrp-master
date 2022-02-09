package cz.muni.fi.rpg.ui.gameMaster

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.combat.ui.ActiveCombatBanner
import cz.frantisekmasa.wfrp_master.common.core.ads.BannerAd
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.TabPager
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.tabs.tab
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.ViewModel
import cz.muni.fi.rpg.ui.gameMaster.encounters.EncountersScreen
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GameMasterScreen(routing: Routing<Route.GameMaster>) {
    val viewModel = ViewModel.GameMaster(routing.route.partyId)
    val party = viewModel.party.collectWithLifecycle(null).value
    val strings = LocalStrings.current.parties

    Scaffold(
        topBar = {
            TopAppBar(
                title = { party?.let { Text(it.name) } },
                navigationIcon = { HamburgerButton() },
                actions = {
                    IconAction(
                        Icons.Rounded.Settings,
                        strings.titleSettings,
                        onClick = {
                            if (party == null) {
                                return@IconAction
                            }

                            routing.navigateTo(Route.PartySettings(party.id))
                        },
                    )
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

        Column(Modifier.fillMaxSize()) {
            ActiveCombatBanner(partyId = party.id, routing = routing)

            TabPager(
                modifier = Modifier.weight(1f),
                fullWidthTabs = true,
            ) {
                val modifier = Modifier.width(screenWidth)

                tab(strings.tabCharacters) {
                    PartySummaryScreen(
                        modifier = modifier,
                        partyId = party.id,
                        viewModel = viewModel,
                        routing = routing,
                        onCharacterOpenRequest = {
                            routing.navigateTo(Route.CharacterDetail(CharacterId(party.id, it.id)))
                        },
                        onCharacterCreateRequest = {
                            routing.navigateTo(Route.CharacterCreation(party.id, it))
                        },
                    )
                }

                tab(strings.tabCalendar) {
                    CalendarScreen(
                        party,
                        modifier = modifier,
                        viewModel = viewModel,
                    )
                }

                tab(strings.tabEncounters) {
                    val encountersViewModel: EncountersViewModel by viewModel { parametersOf(party.id) }
                    EncountersScreen(
                        partyId = party.id,
                        viewModel = encountersViewModel,
                        modifier = modifier,
                        onEncounterClick = {
                            routing.navigateTo(Route.EncounterDetail(EncounterId(party.id, it.id)))
                        },
                    )
                }
            }

            BannerAd(stringResource(R.string.game_master_ad_unit_id))
        }
    }
}
