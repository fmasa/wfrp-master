package cz.muni.fi.rpg.ui.compendium

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.WithConstraintsScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.router.Route
import cz.muni.fi.rpg.ui.router.Routing
import cz.muni.fi.rpg.viewModels.CompendiumViewModel
import cz.muni.fi.rpg.viewModels.PartyViewModel
import org.koin.core.parameter.parametersOf

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
    val viewModel: PartyViewModel by viewModel { parametersOf(routing.route.partyId) }

    TopAppBar(
        title = {
            Column {
                Text(stringResource(R.string.title_compendium))
                viewModel.party.right().observeAsState().value?.let {
                    Subtitle(it.getName())
                }
            }
        },
        navigationIcon = { BackButton(onClick = { routing.backStack.pop() }) },
    )
}

@Composable
private fun WithConstraintsScope.MainContent(routing: Routing<Route.Compendium>) {
    val screenWidth = constraints.maxWidth.toFloat()

    Column {

        val viewModel: CompendiumViewModel by viewModel { parametersOf(routing.route.partyId) }

        val tabs = tabs(maxWidth)
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

private fun tabs(width: Dp): Array<TabScreen<CompendiumViewModel>> = arrayOf(
    TabScreen(R.string.title_character_skills) {
        Box(Modifier.width(width).fillMaxHeight().background(Color.Green)) {

        }
    },
    TabScreen(R.string.title_character_talents) {
        Box(Modifier.width(width).fillMaxHeight().background(Color.Blue)) {

        }
    },
    TabScreen(R.string.title_character_spells) {
        Box(Modifier.width(width).fillMaxHeight().background(Color.Red)) {

        }
    }
)