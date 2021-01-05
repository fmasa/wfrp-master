package cz.frantisekmasa.wfrp_master.combat.ui

import android.widget.Toast
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.IconSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.combat.R
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.viewModel.newViewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@Composable
fun ActiveCombatScreen(routing: Routing<Route.ActiveCombat>) {
    val viewModel: CombatViewModel = newViewModel { parametersOf(routing.route.partyId) }

    AutoCloseOnEndedCombat(viewModel, routing)

    val coroutineScope = rememberCoroutineScope()

    val party = viewModel.party.collectAsState(null).value
    val isGameMaster = AmbientUser.current.id == party?.gameMasterId

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { BackButton(onClick = { routing.backStack.pop() }) },
                title = {
                    Column {
                        Text(stringResource(R.string.title_combat))
                        party?.let { Subtitle(it.getName()) }
                    }
                },
                actions = {
                    if (!isGameMaster) {
                        return@TopAppBar
                    }

                    OptionsAction {
                        DropdownMenuItem(
                            content = { Text(stringResource(R.string.combat_end)) },
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    viewModel.endCombat()
                                }
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (!isGameMaster) {
                // Only GMs should manage turns and rounds
                return@Scaffold
            }

            FloatingActionButton(
                onClick = { coroutineScope.launch(Dispatchers.IO) { viewModel.nextTurn() } }
            ) {
                Icon(vectorResource(R.drawable.ic_round_next))
            }
        }
    ) {
        val combatants = remember { viewModel.combatants() }.collectAsState(null).value
        val round = viewModel.round.collectAsState(null).value
        val turn = viewModel.turn.collectAsState(null).value
        val encounterId = viewModel.activeEncounterId.collectAsState(null).value

        if (combatants == null || round == null || turn == null || party == null || encounterId == null) {
            FullScreenProgress()
            return@Scaffold
        }

        Column {
            SubheadBar(stringResource(R.string.n_round, round))

            ScrollableColumn(Modifier.fillMaxHeight()) {
                CombatantList(
                    coroutineScope = coroutineScope,
                    combatants = combatants,
                    viewModel = viewModel,
                    turn = turn,
                    encounterId = encounterId,
                    isGameMaster = isGameMaster,
                    routing = routing,
                )
            }
        }
    }
}

@Composable
private fun AutoCloseOnEndedCombat(
    viewModel: CombatViewModel,
    routing: Routing<Route.ActiveCombat>
) {
    val context = AmbientContext.current

    LaunchedEffect(routing.route) {
        viewModel.isCombatActive.collect { active ->
            Timber.d("Is combat active? $active")

            if (active) {
                return@collect
            }

            Timber.d("Closing combat screen")

            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_active_combat),
                    Toast.LENGTH_SHORT
                ).show()

                routing.popUpTo(routing.route, inclusive = true)
            }
        }
    }
}

@Composable
private fun CombatantList(
    coroutineScope: CoroutineScope,
    combatants: List<CombatantItem>,
    viewModel: CombatViewModel,
    turn: Int,
    routing: Routing<*>,
    encounterId: EncounterId,
    isGameMaster: Boolean,
) {
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
            modifier = Modifier.combatantClickableModifier(
                isGameMaster,
                encounterId,
                combatant,
                routing
            )
        )
    }
}

private fun Modifier.combatantClickableModifier(
    isGameMaster: Boolean,
    encounterId: EncounterId,
    combatant: CombatantItem,
    routing: Routing<*>
): Modifier =
    composed {
        val userId = AmbientUser.current.id

        when {
            combatant is CombatantItem.Npc && isGameMaster -> tapGestureFilter {
                routing.backStack.push(Route.NpcDetail(combatant.npcId))
            }
            combatant is CombatantItem.Character && (isGameMaster || userId == combatant.userId) -> tapGestureFilter {
                routing.backStack.push(Route.CharacterDetail(combatant.characterId))
            }
            else -> this
        }
    }

@Composable
private fun CombatantListItem(
    onTurn: Boolean,
    combatant: CombatantItem,
    isDragged: Boolean,
    modifier: Modifier
) {
    Surface(
        modifier = modifier,
        elevation = if (isDragged) 6.dp else 2.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
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
                text = { Text(combatant.name) }
            )
        }
    }
}
