package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabContent
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabRow
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.TabScreen
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.muni.fi.rpg.ui.character.skills.CharacterSkillsScreen
import cz.frantisekmasa.wfrp_master.combat.ui.ActiveCombatBanner
import cz.frantisekmasa.wfrp_master.core.ads.BannerAd
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.rememberPagerState
import cz.frantisekmasa.wfrp_master.core.viewModel.PartyViewModel
import cz.frantisekmasa.wfrp_master.inventory.ui.CharacterTrappingsScreen
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.viewModels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

@Composable
fun CharacterDetailScreen(routing: Routing<Route.CharacterDetail>) {
    val characterId = routing.route.characterId

    val viewModel: CharacterViewModel by viewModel { parametersOf(characterId) }
    val partyViewModel: PartyViewModel by viewModel { parametersOf(characterId.partyId) }

    val character = viewModel.character.observeAsState().value
    val party = partyViewModel.party.observeAsState().value

    LaunchedEffect(routing.route.characterId) {
        withContext(Dispatchers.IO) {
            if (!viewModel.characterExists()) {
                withContext(Dispatchers.Main) {
                    routing.navigateTo(
                        Route.CharacterCreation(characterId.partyId, characterId.id),
                        popUpTo = Route.PartyList,
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { HamburgerButton() },
                title = {
                    Column {
                        character?.let { Text(it.getName()) }
                        party?.let { Subtitle(it.getName()) }
                    }
                },
                actions = {
                    TopBarAction(
                        onClick = { routing.navigateTo(Route.CharacterEdit(characterId)) }
                    ) {
                        Icon(
                            vectorResource(R.drawable.ic_edit),
                            stringResource(R.string.icon_edit_character),
                        )
                    }
                }
            )
        }
    ) {
        MainContainer(
            routing = routing,
            character = character,
            viewModel = viewModel,
        )
    }
}

@Composable
private fun MainContainer(
    routing: Routing<Route.CharacterDetail>,
    character: Character?,
    viewModel: CharacterViewModel,
) {
    if (character == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        return
    }

    val characterId = routing.route.characterId

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenWidth = constraints.maxWidth.toFloat()
        val screens = screens(characterId, viewModel, Modifier.width(maxWidth))

        Column(Modifier.fillMaxHeight()) {
            if (! routing.route.comingFromCombat) {
                // Prevent long and confusing back stack when user goes i.e.
                // combat -> character detail -> combat
                ActiveCombatBanner(partyId = characterId.partyId, routing = routing)
            }

            val pagerState = rememberPagerState(screenWidth, screenCount = screens.size)

            TabRow(screens, pagerState)

            TabContent(
                item = character,
                screens = screens,
                state = pagerState,
                modifier = Modifier.weight(1f),
            )

            BannerAd(stringResource(R.string.character_ad_unit_id))
        }
    }
}

@Composable
private fun screens(
    characterId: CharacterId,
    viewModel: CharacterViewModel,
    modifier: Modifier
): Array<TabScreen<Character>> = arrayOf(
    TabScreen(
        R.string.title_misc
    ) { character ->
        CharacterMiscScreen(
            characterId = characterId,
            character = character,
            modifier = modifier,
        )
    },
    TabScreen(R.string.title_character_stats) { character ->
        CharacterCharacteristicsScreen(
            characterId = characterId,
            character = character,
            modifier = modifier,
        )
    },
    TabScreen(R.string.title_character_conditions) { character ->
        CharacterConditionsScreen(
            character = character,
            viewModel = viewModel,
            modifier = modifier,
        )
    },
    TabScreen(R.string.title_character_skills) {
        CharacterSkillsScreen(
            characterVm = viewModel,
            modifier = modifier,
            characterId = characterId,
        )
    },
    TabScreen(R.string.title_character_spells) {
        CharacterSpellsScreen(
            characterId = characterId,
            modifier = modifier,
        )
    },
    TabScreen(R.string.title_character_trappings) {
        CharacterTrappingsScreen(
            characterId = characterId,
            modifier = modifier,
        )
    },
)
