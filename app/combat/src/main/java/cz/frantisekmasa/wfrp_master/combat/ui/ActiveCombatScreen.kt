package cz.frantisekmasa.wfrp_master.combat.ui

import android.widget.Toast
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.IconSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.combat.R
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.viewModel.newViewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        val round = viewModel.round.collectAsState(null).value
        val turn = viewModel.turn.collectAsState(null).value

        if (combatants == null || round == null || turn == null) {
            FullScreenProgress()
            return@Scaffold
        }

        Column {
            SubheadBar(stringResource(R.string.n_round, round))

            ScrollableColumn(Modifier.fillMaxHeight()) {
                CombatantList(combatants, viewModel, turn)
            }
        }
    }
}

@Composable
private fun CombatantList(
    combatants: List<CombatantItem>,
    viewModel: CombatViewModel,
    turn: Int,
) {
    val coroutineScope = rememberCoroutineScope()

    DraggableListFor(
        combatants,
        onReorder = { items ->
            coroutineScope.launch(Dispatchers.IO) {
                viewModel.reorderCombatants(items.map { it.combatant })
            }
        },
        modifier = Modifier.padding(Spacing.bodyPadding),
        itemSpacing = Spacing.small,
    ) { index, combatant, isDragged ->
        CombatantListItem(
            onTurn = index == turn - 1,
            combatant,
            isDragged = isDragged,
        )
    }
}

@Composable
private fun CombatantListItem(onTurn: Boolean, combatant: CombatantItem, isDragged: Boolean) {
    Surface(elevation = if (isDragged) 6.dp else 2.dp, shape = MaterialTheme.shapes.medium) {
        Row(Modifier.preferredHeight(IntrinsicSize.Max)) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .background(
                        if (onTurn)
                            MaterialTheme.colors.primary
                        else MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
                    )
                    .width(Spacing.small)
            )

            ListItem(
                icon = {
                    Icon(
                        when (combatant) {
                            is CombatantItem.Character -> vectorResource(R.drawable.ic_character)
                            is CombatantItem.Npc -> vectorResource(R.drawable.ic_npc)
                        },
                        modifier = Modifier.size(IconSize)
                    )
                },
                text = {
                    Text(
                        when (combatant) {
                            is CombatantItem.Character -> combatant.character.getName()
                            is CombatantItem.Npc -> combatant.npc.name
                        }
                    )
                }
            )
        }
    }
}
