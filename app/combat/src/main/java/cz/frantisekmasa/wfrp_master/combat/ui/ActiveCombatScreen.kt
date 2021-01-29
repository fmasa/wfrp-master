package cz.frantisekmasa.wfrp_master.combat.ui

import android.widget.Toast
import androidx.compose.animation.asDisposableClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.IconSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import cz.frantisekmasa.wfrp_master.combat.R
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Wounds
import cz.frantisekmasa.wfrp_master.core.ads.BannerAd
import cz.frantisekmasa.wfrp_master.core.auth.AmbientUser
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.components.CharacteristicsTable
import cz.frantisekmasa.wfrp_master.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import kotlinx.coroutines.*
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@Composable
fun ActiveCombatScreen(routing: Routing<Route.ActiveCombat>) {
    val viewModel: CombatViewModel by viewModel { parametersOf(routing.route.partyId) }

    AutoCloseOnEndedCombat(viewModel, routing)

    val coroutineScope = rememberCoroutineScope()

    val party = viewModel.party.observeAsState(null).value
    val combatants = remember { viewModel.combatants() }.observeAsState().value
    val isGameMaster = AmbientUser.current.id == party?.gameMasterId

    var openedCombatant by remember { mutableStateOf<CombatantItem?>(null) }
    val bottomSheetState = rememberNotSavedModalBottomSheetState()

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = MaterialTheme.shapes.small,
        sheetContent = {
            if (!bottomSheetState.isVisible) {
                Box(Modifier.height(1.dp))
                return@ModalBottomSheetLayout
            }

            openedCombatant?.let { combatant ->
                /*
             There are two things happening
             1. We always need fresh version of given combatant,
                because user may have edited combatant, i.e. by changing her advantage.
                So we cannot used value from saved mutable state.
             2. We have to show sheet only if fresh combatant is in the collection,
                because she may have been removed from combat.
             */
                val freshCombatant =
                    combatants?.firstOrNull { it.areSameEntity(combatant) } ?: return@let

                CombatantSheet(freshCombatant, routing, viewModel)
            }
        },
    ) {
        Column {
            Scaffold(
                modifier = Modifier.weight(1f),
                topBar = {
                    TopAppBar(
                        navigationIcon = { BackButton(onClick = { routing.pop() }) },
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
            ) {
                val round = viewModel.round.observeAsState().value
                val turn = viewModel.turn.observeAsState().value

                if (combatants == null || round == null || turn == null || party == null) {
                    FullScreenProgress()
                    return@Scaffold
                }

                Column {
                    Column(
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        CombatantList(
                            coroutineScope = coroutineScope,
                            combatants = combatants,
                            viewModel = viewModel,
                            turn = turn,
                            isGameMaster = isGameMaster,
                            onCombatantClicked = {
                                openedCombatant = it
                                bottomSheetState.show()
                            }
                        )
                    }

                    if (isGameMaster) {
                        BottomBar(turn, round, viewModel)
                    }
                }
            }

            BannerAd(stringResource(R.string.combat_ad_unit_id))
        }
    }
}

private fun canEditCombatant(userId: String, isGameMaster: Boolean, combatant: CombatantItem) =
    isGameMaster || (combatant is CombatantItem.Character && combatant.userId == userId)

@Composable
private fun BottomBar(turn: Int, round: Int, viewModel: CombatViewModel) {
    val coroutineScope = rememberCoroutineScope()

    BottomAppBar(backgroundColor = MaterialTheme.colors.surface) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                enabled = round > 1 || turn > 1,
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) { viewModel.previousTurn() }
                },
            ) {
                Icon(
                    vectorResource(R.drawable.ic_arrow_back),
                    stringResource(R.string.icon_previous_turn),
                )
            }

            Text(stringResource(R.string.n_round, round))

            IconButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) { viewModel.nextTurn() }
                }
            ) {
                Icon(
                    vectorResource(R.drawable.ic_arrow_forward),
                    stringResource(R.string.icon_next_turn),
                )
            }
        }
    }
}

@Composable
private fun rememberNotSavedModalBottomSheetState(): ModalBottomSheetState {
    val disposableClock = AmbientAnimationClock.current.asDisposableClock()
    return remember(disposableClock) {
        ModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            clock = disposableClock,
            animationSpec = SwipeableDefaults.AnimationSpec,
            confirmStateChange = { true },
        )
    }
}

@Composable
private fun AutoCloseOnEndedCombat(
    viewModel: CombatViewModel,
    routing: Routing<Route.ActiveCombat>
) {
    val context = AmbientContext.current
    val lifecycleOwner = AmbientLifecycleOwner.current

    val observer: Observer<Boolean> = remember(routing) {
        Observer { active ->
            if (active) {
                return@Observer
            }

            Timber.d("Closing combat screen")

            Toast.makeText(
                context,
                context.getString(R.string.no_active_combat),
                Toast.LENGTH_SHORT
            ).show()

            routing.pop()
        }
    }

    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(viewModel, lifecycleOwner, context) {
        coroutineScope.launch(Dispatchers.Default) {
            delay(3_000)
            withContext(Dispatchers.Main) {
                viewModel.isCombatActive.observe(lifecycleOwner, observer)
            }
        }

        onDispose { viewModel.isCombatActive.removeObserver(observer) }
    }
}

@Composable
private fun CombatantList(
    coroutineScope: CoroutineScope,
    combatants: List<CombatantItem>,
    viewModel: CombatViewModel,
    turn: Int,
    isGameMaster: Boolean,
    onCombatantClicked: (CombatantItem) -> Unit,
) {
    val userId = AmbientUser.current.id

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
            modifier = when {
                canEditCombatant(userId, isGameMaster, combatant) -> Modifier.tapGestureFilter {
                    onCombatantClicked(combatant)
                }
                else -> Modifier
            }
        )
    }
}

@Composable
private fun CombatantSheet(
    combatant: CombatantItem,
    routing: Routing<*>,
    viewModel: CombatViewModel
) {
    Column(
        Modifier.verticalScroll(rememberScrollState())
            .padding(Spacing.bodyPadding),
        verticalArrangement = Arrangement.spacedBy(Spacing.small),
    ) {
        Text(
            combatant.name,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )

        TextButton(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                routing.navigateTo(
                    when (combatant) {
                        is CombatantItem.Npc -> Route.NpcDetail(combatant.npcId)
                        is CombatantItem.Character -> Route.CharacterDetail(combatant.characterId)
                    }
                )
            },
            content = { Text(stringResource(R.string.button_detail).toUpperCase(Locale.current)) },
        )

        Row(Modifier.padding(bottom = Spacing.medium)) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                CombatantWounds(combatant, viewModel)
            }

            Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                CombatantAdvantage(combatant, viewModel)
            }
        }

        CharacteristicsTable(combatant.characteristics)
    }
}

@Composable
private fun CombatantWounds(combatant: CombatantItem, viewModel: CombatViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val updateWounds = { wounds: Wounds ->
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.updateWounds(combatant, wounds)
        }
    }

    val wounds = combatant.wounds

    NumberPicker(
        label = stringResource(R.string.label_wounds),
        value = wounds.current,
        onIncrement = { updateWounds(wounds.restore(1)) },
        onDecrement = { updateWounds(wounds.lose(1)) },
    )
}


@Composable
private fun CombatantAdvantage(combatant: CombatantItem, viewModel: CombatViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val updateAdvantage = { advantage: Int ->
        coroutineScope.launch(Dispatchers.IO) {
            viewModel.updateAdvantage(combatant.combatant, advantage)
        }
    }

    val advantage = combatant.combatant.advantage

    NumberPicker(
        label = stringResource(R.string.label_advantage),
        value = advantage,
        onIncrement = { updateAdvantage(advantage + 1) },
        onDecrement = { updateAdvantage(advantage - 1) },
    )
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
                    val iconModifier = Modifier.size(IconSize)

                    when (combatant) {
                        is CombatantItem.Character -> {
                            Icon(
                                vectorResource(R.drawable.ic_character),
                                stringResource(R.string.icon_combatant_character),
                                iconModifier,
                            )
                        }
                        is CombatantItem.Npc -> {
                            Icon(
                                vectorResource(R.drawable.ic_npc),
                                stringResource(R.string.icon_combatant_npc),
                                iconModifier,
                            )
                        }
                    }
                },
                text = { Text(combatant.name) }
            )
        }
    }
}
