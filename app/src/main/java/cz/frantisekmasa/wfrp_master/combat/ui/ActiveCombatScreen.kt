package cz.frantisekmasa.wfrp_master.combat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Wounds
import cz.frantisekmasa.wfrp_master.common.core.ads.BannerAd
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacteristicsTable
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@Composable
fun ActiveCombatScreen(routing: Routing<Route.ActiveCombat>) {
    val viewModel: CombatViewModel by viewModel { parametersOf(routing.route.partyId) }

    AutoCloseOnEndedCombat(viewModel, routing)

    val coroutineScope = rememberCoroutineScope()

    val party = viewModel.party.collectWithLifecycle(null).value
    val combatants = remember { viewModel.combatants() }.collectWithLifecycle(null).value
    val isGameMaster = LocalUser.current.id == party?.gameMasterId

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
            val strings = LocalStrings.current

            Scaffold(
                modifier = Modifier.weight(1f),
                topBar = {
                    TopAppBar(
                        navigationIcon = { BackButton(onClick = { routing.pop() }) },
                        title = {
                            Column {
                                Text(strings.combat.title)
                                party?.let { Subtitle(it.name) }
                            }
                        },
                        actions = {
                            if (!isGameMaster) {
                                return@TopAppBar
                            }

                            OptionsAction {
                                DropdownMenuItem(
                                    content = { Text(strings.combat.buttonEndCombat) },
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
                val round = viewModel.round.collectWithLifecycle(null).value
                val turn = viewModel.turn.collectWithLifecycle(null).value

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
                                coroutineScope.launch { bottomSheetState.show() }
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
    val strings = LocalStrings.current.combat

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
                    Icons.Rounded.ArrowBack,
                    strings.iconPreviousTurn,
                )
            }

            Text(strings.nthRound(round))

            IconButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) { viewModel.nextTurn() }
                }
            ) {
                Icon(
                    Icons.Rounded.ArrowForward,
                    strings.iconNextTurn,
                )
            }
        }
    }
}

@Composable
private fun rememberNotSavedModalBottomSheetState(): ModalBottomSheetState {
    return remember {
        ModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
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
    val isCombatActive = viewModel.isCombatActive.collectWithLifecycle(true).value
    val message = LocalStrings.current.combat.messages.noActiveCombat
    val snackbarHostState = LocalPersistentSnackbarHolder.current

    if (!isCombatActive) {
        LaunchedEffect(Unit) {
            Napier.d("Closing combat screen")

            snackbarHostState.showSnackbar(message)

            routing.pop()
        }
        val coroutineScope = rememberCoroutineScope()
        SideEffect {
        }
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
    val userId = LocalUser.current.id

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
                canEditCombatant(userId, isGameMaster, combatant) -> Modifier.clickable {
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
        Modifier
            .verticalScroll(rememberScrollState())
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
                        is CombatantItem.Character -> Route.CharacterDetail(combatant.characterId, comingFromCombat = true)
                    }
                )
            },
            content = { Text(LocalStrings.current.commonUi.buttonDetail.uppercase()) },
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
        label = LocalStrings.current.points.wounds,
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
        label = LocalStrings.current.combat.labelAdvantage,
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
        Row(Modifier.height(IntrinsicSize.Max)) { /* TODO: REMOVE COMMENT */
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
                    when (combatant) {
                        is CombatantItem.Character -> {
                            CharacterAvatar(combatant.avatarUrl, ItemIcon.Size.Small)
                        }
                        is CombatantItem.Npc -> {
                            ItemIcon(Resources.Drawable.Npc, ItemIcon.Size.Small)
                        }
                    }
                },
                text = { Text(combatant.name) }
            )
        }
    }
}
