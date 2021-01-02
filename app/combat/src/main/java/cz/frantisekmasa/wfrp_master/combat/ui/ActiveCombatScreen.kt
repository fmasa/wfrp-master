package cz.frantisekmasa.wfrp_master.combat.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ButtonDefaults.IconSize
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import cz.frantisekmasa.wfrp_master.combat.R
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.viewModel.newViewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

@Composable
fun ActiveCombatScreen(routing: Routing<Route.ActiveCombat>) {
    val viewModel: CombatViewModel = newViewModel { parametersOf(routing.route.partyId) }
    val context = AmbientContext.current

    LaunchedEffect(routing.route.partyId) {
        if (!withContext(Dispatchers.IO) { viewModel.hasActiveCombat() }) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_active_combat),
                    Toast.LENGTH_SHORT
                ).show()
                routing.backStack.pop()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(onClick = { routing.backStack.pop() }) },
                title = {
                    Column {
                        Text(stringResource(R.string.title_combat))
                        viewModel.party.collectAsState(null).value?.let { Subtitle(it.getName()) }
                    }
                },
            )
        }
    ) {
        val combatants = remember { viewModel.combatants() }.collectAsState(null).value

        if (combatants == null) {
            FullScreenProgress()
            return@Scaffold
        }
        LazyColumn(contentPadding = PaddingValues(Spacing.bodyPadding)) {
            items(items = combatants) { CombatantListItem(it) }
        }
    }
}

@Composable
private fun CombatantListItem(combatantItem: CombatantItem) {
    ListItem(
        icon = {
            Icon(
                when (combatantItem) {
                    is CombatantItem.Character -> vectorResource(R.drawable.ic_character)
                    is CombatantItem.Npc -> vectorResource(R.drawable.ic_npc)
                },
                modifier = Modifier.size(IconSize)
            )
        },
        text = {
            Text(
                when (combatantItem) {
                    is CombatantItem.Character -> combatantItem.character.getName()
                    is CombatantItem.Npc -> combatantItem.npc.name
                }
            )
        }
    )
}