package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.combat.ui.StartCombatDialog
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardItem
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.viewModel.PartyViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.CardTitle
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun EncounterDetailScreen(routing: Routing<Route.EncounterDetail>) {
    val encounterId = routing.route.encounterId
    val viewModel: EncounterDetailViewModel by viewModel { parametersOf(encounterId) }

    var startCombatDialogVisible by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val partyId = encounterId.partyId
            val encounter = viewModel.encounter.observeAsState().value

            TopAppBar(
                title = {
                    Column {
                        encounter?.let { Text(it.name) }

                        val partyViewModel: PartyViewModel by viewModel { parametersOf(partyId) }
                        partyViewModel.party.observeAsState().value?.let {
                            Subtitle(it.getName())
                        }
                    }
                },
                navigationIcon = {
                    BackButton(onClick = { routing.pop() })
                },
                actions = {
                    encounter?.let {
                        TopAppBarActions(
                            routing = routing,
                            encounter = it,
                            partyId = partyId,
                            viewModel = viewModel,
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_encounter),
                        VisualOnlyIconDescription,
                        Modifier.width(24.dp),
                    )
                },
                text = { Text(stringResource(R.string.title_start_combat)) },
                onClick = { startCombatDialogVisible = true },
            )
        },
        content = {
            MainContainer(routing, viewModel)

            if (startCombatDialogVisible) {
                StartCombatDialog(
                    encounterId = encounterId,
                    onDismissRequest = { startCombatDialogVisible = false },
                    onComplete = {
                        startCombatDialogVisible = false
                        routing.navigateTo(Route.ActiveCombat(partyId = encounterId.partyId))
                    },
                )
            }
        }
    )
}

@Composable
private fun TopAppBarActions(
    routing: Routing<Route.EncounterDetail>,
    encounter: Encounter,
    partyId: PartyId,
    viewModel: EncounterDetailViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var editDialogOpened by rememberSaveable { mutableStateOf(false) }

    if (editDialogOpened) {
        EncounterDialog(
            existingEncounter = encounter,
            partyId = partyId,
            onDismissRequest = { editDialogOpened = false },
        )
    }

    IconButton(onClick = { editDialogOpened = true }) {
        Icon(
            painterResource(R.drawable.ic_edit),
            stringResource(R.string.title_encounter_edit),
            tint = contentColorFor(MaterialTheme.colors.primarySurface),
        )
    }

    OptionsAction {
        DropdownMenuItem(
            onClick = {
                AlertDialog.Builder(context)
                    .setMessage(R.string.question_remove_encounter)
                    .setPositiveButton(R.string.remove) { _, _ ->
                        coroutineScope.launch(Dispatchers.IO) {
                            viewModel.remove()
                            withContext(Dispatchers.Main) { routing.pop() }
                        }
                    }.setNegativeButton(R.string.button_cancel, null)
                    .create()
                    .show()
            }
        ) {
            Text(stringResource(R.string.remove))
        }
    }
}

@Composable
private fun MainContainer(
    routing: Routing<Route.EncounterDetail>,
    viewModel: EncounterDetailViewModel
) {
    val encounterId = routing.route.encounterId
    val coroutineScope = rememberCoroutineScope { EmptyCoroutineContext + Dispatchers.IO }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = 6.dp, bottom = Spacing.bottomPaddingUnderFab),
    ) {
        DescriptionCard(viewModel)
        CombatantsCard(
            viewModel,
            onCreateRequest = { routing.navigateTo(Route.NpcCreation(encounterId)) },
            onEditRequest = { routing.navigateTo(Route.NpcDetail(it)) },
            onRemoveRequest = { viewModel.removeNpc(it) },
            onDuplicateRequest = { coroutineScope.launch { viewModel.duplicateNpc(it.npcId) } }
        )
    }
}

@Composable
private fun DescriptionCard(viewModel: EncounterDetailViewModel) {
    CardContainer(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        CardTitle(R.string.title_description)

        val encounter = viewModel.encounter.observeAsState().value

        if (encounter == null) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@CardContainer
        }

        Text(encounter.description, Modifier.padding(horizontal = 8.dp))
    }
}

@Composable
private fun CombatantsCard(
    viewModel: EncounterDetailViewModel,
    onCreateRequest: () -> Unit,
    onEditRequest: (NpcId) -> Unit,
    onRemoveRequest: (NpcId) -> Unit,
    onDuplicateRequest: (NpcId) -> Unit,
) {
    CardContainer(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        CardTitle(R.string.title_npcs)

        val npcs = viewModel.npcs.observeAsState().value

        if (npcs == null) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            return@CardContainer
        }

        Column(Modifier.fillMaxWidth()) {

            if (npcs.isEmpty()) {
                EmptyUI(
                    textId = R.string.no_npcs_prompt,
                    drawableResourceId = R.drawable.ic_npc,
                    size = EmptyUI.Size.Small,
                )
            } else {
                NpcList(
                    npcs,
                    onEditRequest = onEditRequest,
                    onRemoveRequest = onRemoveRequest,
                    onDuplicateRequest = onDuplicateRequest,
                )
            }

            Box(
                Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                PrimaryButton(R.string.title_npc_add, onClick = onCreateRequest)
            }
        }
    }
}

@Composable
private fun NpcList(
    npcs: List<NpcListItem>,
    onEditRequest: (NpcId) -> Unit,
    onRemoveRequest: (NpcId) -> Unit,
    onDuplicateRequest: (NpcId) -> Unit,
) {
    for (npc in npcs) {
        val alpha = if (npc.alive) ContentAlpha.high else ContentAlpha.disabled

        CompositionLocalProvider(LocalContentAlpha provides alpha) {
            CardItem(
                name = npc.name,
                iconRes = if (npc.alive) R.drawable.ic_npc else R.drawable.ic_dead,
                onClick = { onEditRequest(npc.id) },
                contextMenuItems = listOf(
                    ContextMenu.Item(
                        text = stringResource(R.string.button_duplicate),
                        onClick = { onDuplicateRequest(npc.id) },
                    ),
                    ContextMenu.Item(
                        text = stringResource(R.string.remove),
                        onClick = { onRemoveRequest(npc.id) },
                    ),
                ),
            )
        }
    }
}
