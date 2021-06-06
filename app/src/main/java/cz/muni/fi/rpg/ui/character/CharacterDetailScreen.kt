package cz.muni.fi.rpg.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import cz.frantisekmasa.wfrp_master.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.muni.fi.rpg.ui.character.skills.CharacterSkillsScreen
import cz.frantisekmasa.wfrp_master.combat.ui.ActiveCombatBanner
import cz.frantisekmasa.wfrp_master.core.ads.BannerAd
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.tabs.*
import cz.frantisekmasa.wfrp_master.core.viewModel.PartyViewModel
import cz.frantisekmasa.wfrp_master.inventory.ui.CharacterTrappingsScreen
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.frantisekmasa.wfrp_master.religion.ui.CharacterReligionScreen
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
                            painterResource(R.drawable.ic_edit),
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

    Column(Modifier.fillMaxSize()) {
        if (! routing.route.comingFromCombat) {
            // Prevent long and confusing back stack when user goes i.e.
            // combat -> character detail -> combat
            ActiveCombatBanner(partyId = characterId.partyId, routing = routing)
        }

        TabPager(Modifier.weight(1f)) {
            val modifier = Modifier.width(screenWidth)

            tab(R.string.title_misc) {
                CharacterMiscScreen(
                    characterId = characterId,
                    character = character,
                    modifier = modifier,
                )
            }

            tab(R.string.title_character_stats) {
                CharacterCharacteristicsScreen(
                    characterId = characterId,
                    character = character,
                    modifier = modifier,
                )
            }

            tab(R.string.title_character_conditions) {
                CharacterConditionsScreen(
                    character = character,
                    viewModel = viewModel,
                    modifier = modifier,
                )
            }

            tab(R.string.title_character_skills) {
                CharacterSkillsScreen(
                    characterVm = viewModel,
                    modifier = modifier,
                    characterId = characterId,
                )
            }

            tab(R.string.title_character_spells) {
                CharacterSpellsScreen(
                    characterId = characterId,
                    modifier = modifier,
                )
            }

            tab(R.string.title_character_religion) {
                CharacterReligionScreen(
                    characterId = characterId,
                    modifier = modifier,
                )
            }

            tab(R.string.title_character_trappings) {
                CharacterTrappingsScreen(
                    characterId = characterId,
                    modifier = modifier,
                )
            }
        }

        BannerAd(stringResource(R.string.character_ad_unit_id))
    }
}

