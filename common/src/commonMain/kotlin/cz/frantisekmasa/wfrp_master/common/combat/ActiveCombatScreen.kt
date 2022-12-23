package cz.frantisekmasa.wfrp_master.common.combat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.conditions.ConditionIcon
import cz.frantisekmasa.wfrp_master.common.core.auth.LocalUser
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Advantage
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.GroupAdvantage
import cz.frantisekmasa.wfrp_master.common.core.domain.party.settings.AdvantageSystem
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.StatBlock
import cz.frantisekmasa.wfrp_master.common.core.ui.StatBlockData
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.DialogProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FlowRow
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.encounters.CombatantItem
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Wounds
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.npcs.NpcDetailScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActiveCombatScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        val viewModel: CombatScreenModel = rememberScreenModel(arg = partyId)

        val coroutineScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow

        val party = viewModel.party.collectWithLifecycle(null).value
        val combatants = remember { viewModel.combatants() }.collectWithLifecycle(null).value
        val isGameMaster = LocalUser.current.id == party?.gameMasterId

        val (openedCombatant, setOpenedCombatant) = remember { mutableStateOf<CombatantItem?>(null) }
        val freshCombatant = if (openedCombatant != null)
            combatants?.firstOrNull { it.areSameEntity(openedCombatant) }
        else null

        val bottomSheetState = rememberModalBottomSheetState(
            ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
        )

        val isGroupAdvantageSystemEnabled by derivedStateOf {
            party?.settings?.advantageSystem == AdvantageSystem.GROUP_ADVANTAGE
        }

        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetShape = MaterialTheme.shapes.small,
            sheetContent = {
                Box(Modifier.height(1.dp))

                if (openedCombatant == null) {
                    DialogProgress()
                    return@ModalBottomSheetLayout
                }

                if (party == null || freshCombatant == null) {
                    return@ModalBottomSheetLayout
                }

                /*
                 There are two things happening:
                 1. We always need fresh version of given combatant,
                    because user may have edited combatant, i.e. by changing her advantage.
                    So we cannot used value from saved mutable state.
                 2. We have to show sheet only if fresh combatant is in the collection,
                    because she may have been removed from combat.
                 */

                val advantageCap by derivedStateOf {
                    party.settings.advantageCap.calculate(freshCombatant.characteristics)
                }

                val combatantId = freshCombatant.combatant.id
                CombatantSheet(
                    combatant = freshCombatant,
                    viewModel = viewModel,
                    advantageCap = advantageCap,
                    isGroupAdvantageSystemEnabled = isGroupAdvantageSystemEnabled,
                    onDetailOpenRequest = {
                        navigator.push(
                            when (freshCombatant) {
                                is CombatantItem.Npc -> NpcDetailScreen(freshCombatant.npcId)
                                is CombatantItem.Character -> CharacterDetailScreen(
                                    freshCombatant.characterId,
                                    comingFromCombat = true,
                                )
                            }
                        )
                        coroutineScope.launch { bottomSheetState.hide() }
                    },
                    onRemoveRequest = combatantId?.let {
                        {
                            coroutineScope.launch {
                                if (combatants?.size == 1) {
                                    viewModel.endCombat()
                                    navigator.pop()
                                } else {
                                    viewModel.removeCombatant(combatantId)
                                    setOpenedCombatant(null)
                                    bottomSheetState.hide()
                                }
                            }
                        }
                    }
                )
            },
        ) {
            Column {
                val strings = LocalStrings.current

                Scaffold(
                    modifier = Modifier.weight(1f),
                    topBar = {
                        TopAppBar(
                            navigationIcon = { BackButton() },
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
                                                navigator.pop()
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
                    val groupAdvantage = viewModel.groupAdvantage.collectWithLifecycle(null).value

                    if (
                        combatants == null ||
                        round == null ||
                        turn == null ||
                        party == null ||
                        groupAdvantage == null
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                CircularProgressIndicator()
                                Text(LocalStrings.current.combat.messages.waitingForCombat)
                            }
                        }
                        return@Scaffold
                    }

                    Column {
                        Column(
                            Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                        ) {
                            key(combatants) {
                                CombatantList(
                                    coroutineScope = coroutineScope,
                                    combatants = combatants,
                                    viewModel = viewModel,
                                    turn = turn,
                                    isGameMaster = isGameMaster,
                                    onCombatantClicked = {
                                        setOpenedCombatant(it)
                                        coroutineScope.launch { bottomSheetState.show() }
                                    },
                                )
                            }
                        }

                        if (isGroupAdvantageSystemEnabled) {
                            GroupAdvantageBar(
                                groupAdvantage = groupAdvantage,
                                isGameMaster = isGameMaster,
                                screenModel = viewModel,
                            )
                        }

                        if (isGameMaster) {
                            BottomBar(turn, round, viewModel)
                        }
                    }
                }
            }
        }
    }

    private fun canEditCombatant(userId: String, isGameMaster: Boolean, combatant: CombatantItem) =
        isGameMaster || (combatant is CombatantItem.Character && combatant.userId == userId)

    @Composable
    private fun GroupAdvantageBar(
        groupAdvantage: GroupAdvantage,
        isGameMaster: Boolean,
        screenModel: CombatScreenModel,
    ) {
        Surface(elevation = 2.dp) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = Spacing.small),
            ) {
                Text(
                    LocalStrings.current.combat.labelAdvantage,
                    fontWeight = FontWeight.Bold,
                )

                val strings = LocalStrings.current.combat

                Row {
                    if (isGameMaster) {
                        val coroutineScope = rememberCoroutineScope()
                        val update = { groupAdvantage: GroupAdvantage ->
                            coroutineScope.launch(Dispatchers.IO) {
                                screenModel.updateGroupAdvantage(groupAdvantage)
                            }
                        }

                        AdvantagePicker(
                            label = strings.labelAllies,
                            value = groupAdvantage.allies,
                            onChange = { update(groupAdvantage.copy(allies = it)) },
                            modifier = Modifier.weight(1f),
                        )

                        AdvantagePicker(
                            label = strings.labelEnemies,
                            value = groupAdvantage.enemies,
                            onChange = { update(groupAdvantage.copy(enemies = it)) },
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        AdvantageValue(
                            label = strings.labelAllies,
                            value = groupAdvantage.allies,
                            modifier = Modifier.weight(1f),
                        )

                        AdvantageValue(
                            label = strings.labelEnemies,
                            value = groupAdvantage.enemies,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun AdvantageValue(
        label: String,
        value: Advantage,
        modifier: Modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .padding(vertical = 4.dp)
                .then(modifier)
        ) {
            Text(label, style = MaterialTheme.typography.subtitle1)
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.h5,
            )
        }
    }

    @Composable
    private fun AdvantagePicker(
        label: String,
        value: Advantage,
        onChange: (Advantage) -> Unit,
        modifier: Modifier = Modifier,
    ) {
        NumberPicker(
            label = label,
            value = value.value,
            onIncrement = { onChange(value.inc()) },
            onDecrement = { onChange(value.dec()) },
            modifier = modifier,
        )
    }

    @Composable
    private fun BottomBar(turn: Int, round: Int, viewModel: CombatScreenModel) {
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
    private fun CombatantList(
        coroutineScope: CoroutineScope,
        combatants: List<CombatantItem>,
        viewModel: CombatScreenModel,
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
                isGameMaster = isGameMaster,
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
        viewModel: CombatScreenModel,
        isGroupAdvantageSystemEnabled: Boolean,
        advantageCap: Advantage,
        onRemoveRequest: (() -> Unit)?,
        onDetailOpenRequest: () -> Unit,
    ) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.large),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            val navigator = LocalNavigator.currentOrThrow
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    combatant.name,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.clickable(onClick = onDetailOpenRequest)
                )

                if (onRemoveRequest != null) {
                    var contextMenuExpanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { contextMenuExpanded = true }) {
                            Icon(
                                Icons.Filled.MoreVert,
                                LocalStrings.current.commonUi.labelOpenContextMenu,
                            )
                        }
                        DropdownMenu(
                            expanded = contextMenuExpanded,
                            onDismissRequest = { contextMenuExpanded = false },
                        ) {
                            DropdownMenuItem(onClick = onRemoveRequest) {
                                Text(LocalStrings.current.combat.buttonRemoveCombatant)
                            }
                        }
                    }
                }
            }

            ConditionsBox(
                modifier = Modifier.padding(bottom = Spacing.small),
                combatant = combatant,
                screenModel = viewModel,
            )

            var statBlockData: StatBlockData? by rememberSaveable { mutableStateOf(null) }

            StatBlock(combatant.characteristics, statBlockData)

            LaunchedEffect(combatant.combatant.id) {
                withContext(Dispatchers.IO) {
                    statBlockData = viewModel.getStatBlockData(combatant)
                }
            }

            Divider()

            Row(Modifier.padding(bottom = Spacing.medium)) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                    CombatantWounds(combatant, viewModel)
                }

                if (!isGroupAdvantageSystemEnabled) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                        CombatantAdvantage(combatant, viewModel, advantageCap)
                    }
                }
            }
        }
    }

    @Composable
    private fun CombatantWounds(combatant: CombatantItem, viewModel: CombatScreenModel) {
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
    private fun CombatantAdvantage(
        combatant: CombatantItem,
        viewModel: CombatScreenModel,
        advantageCap: Advantage,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val updateAdvantage = { advantage: Advantage ->
            coroutineScope.launch(Dispatchers.IO) {
                viewModel.updateAdvantage(combatant.combatant, advantage)
            }
        }

        val advantage = combatant.combatant.advantage

        AdvantagePicker(
            label = LocalStrings.current.combat.labelAdvantage,
            value = advantage,
            onChange = { updateAdvantage(it.coerceAtMost(advantageCap)) }
        )
    }

    @Composable
    private fun CombatantListItem(
        onTurn: Boolean,
        combatant: CombatantItem,
        isDragged: Boolean,
        isGameMaster: Boolean,
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

                Column {
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
                        text = {
                            Column {
                                Text(combatant.name)

                                if (isGameMaster) {
                                    WoundsBar(combatant.wounds.current, combatant.wounds.max)
                                }
                            }
                        },
                        trailing = {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("I: ${combatant.combatant.initiative}")

                                val advantage = combatant.combatant.advantage

                                if (advantage > Advantage.ZERO) {
                                    Text("A: $advantage", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    )

                    val conditions by derivedStateOf { combatant.conditions }

                    if (!conditions.areEmpty()) {
                        Row(
                            modifier = Modifier
                                .padding(bottom = Spacing.small)
                                .padding(horizontal = Spacing.medium)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(
                                Spacing.small,
                                Alignment.End,
                            )
                        ) {
                            val conditionsList by derivedStateOf { conditions.toList() }
                            conditionsList.forEach { (condition, count) ->
                                key(condition, count) {
                                    repeat(count) { ConditionIcon(condition, size = 20.dp) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConditionsBox(
    modifier: Modifier,
    combatant: CombatantItem,
    screenModel: CombatScreenModel,
) {
    var conditionsDialogOpened by remember { mutableStateOf(false) }

    if (conditionsDialogOpened) {
        ConditionsDialog(
            combatantItem = combatant,
            screenModel = screenModel,
            onDismissRequest = { conditionsDialogOpened = false },
        )
    }

    Box(
        modifier = modifier.clickable { conditionsDialogOpened = true }
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {

        val conditions = combatant.conditions

        if (conditions.areEmpty()) {
            Text(LocalStrings.current.combat.messages.noConditions)
        } else {
            FlowRow(verticalSpacing = Spacing.small, horizontalSpacing = Spacing.small) {
                conditions.toList().forEach { (condition, count) ->
                    repeat(count) { index ->
                        key(condition, index) {
                            ConditionIcon(condition, size = 28.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WoundsBar(current: Int, max: Int) {
    if (max == 0) {
        return
    }

    Row {
        Text("$current/$max", style = MaterialTheme.typography.caption)

        Surface(
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier
                .padding(top = Spacing.small, start = Spacing.tiny)
                .fillMaxWidth()
        ) {
            Box(
                Modifier.fillMaxWidth()
                    .background(Color(183, 28, 28))
            ) {
                Box(
                    Modifier.fillMaxWidth(current.toFloat() / max)
                        .background(
                            if (MaterialTheme.colors.isLight)
                                Color(76, 175, 80)
                            else Color(129, 199, 132)
                        )
                        .height(4.dp)
                )
            }
        }
    }
}
