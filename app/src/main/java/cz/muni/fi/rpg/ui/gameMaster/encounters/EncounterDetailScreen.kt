package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Npc
import cz.frantisekmasa.wfrp_master.combat.ui.StartCombatDialog
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.CardContainer
import cz.frantisekmasa.wfrp_master.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.TopBarAction
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.fragmentManager
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.composables.*
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import cz.muni.fi.rpg.viewModels.PartyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import java.util.*

@Composable
fun EncounterDetailScreen(routing: Routing<Route.EncounterDetail>) {
    val encounterId = routing.route.encounterId
    val viewModel: EncounterDetailViewModel by viewModel { parametersOf(encounterId) }

    var startCombatDialogVisible by savedInstanceState { false }

    Scaffold(
        topBar = {
            val partyId = encounterId.partyId
            val encounter = viewModel.encounter.collectAsState(null).value

            TopAppBar(
                title = {
                    Column {
                        encounter?.let { Text(it.name) }

                        val partyViewModel: PartyViewModel by viewModel { parametersOf(partyId) }
                        partyViewModel.party.collectAsState(null).value?.let {
                            Subtitle(it.getName())
                        }
                    }
                },
                navigationIcon = {
                    BackButton(onClick = { routing.backStack.pop() })
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
                icon = { Icon(vectorResource(R.drawable.ic_encounter), Modifier.width(24.dp)) },
                text = { Text(stringResource(R.string.title_start_combat)) },
                onClick = { startCombatDialogVisible = true },
            )
        },
        bodyContent = {
            MainContainer(routing, viewModel)

            if (startCombatDialogVisible) {
                StartCombatDialog(
                    encounterId = encounterId,
                    onDismissRequest = { startCombatDialogVisible = false },
                    onComplete = {
                        startCombatDialogVisible = false
                        routing.backStack.push(Route.ActiveCombat(partyId = encounterId.partyId))
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
    partyId: UUID,
    viewModel: EncounterDetailViewModel
) {
    val context = AmbientContext.current
    val fragmentManager = fragmentManager()
    val coroutineScope = rememberCoroutineScope()

    TopBarAction(
        onClick = {
            EncounterDialog.newInstance(
                partyId,
                EncounterDialog.Defaults(
                    encounter.id,
                    encounter.name,
                    encounter.description
                )
            ).show(fragmentManager, null)
        }
    ) {
        Icon(vectorResource(R.drawable.ic_edit))
    }


    val menuState = mutableStateOf(false)
    TopBarAction(onClick = { menuState.value = true }) {
        Icon(vectorResource(R.drawable.ic_more))
    }

    ContextMenu(
        items = listOf(
            ContextMenu.Item(
                text = stringResource(R.string.remove),
                onClick = {
                    AlertDialog.Builder(context)
                        .setMessage(R.string.question_remove_encounter)
                        .setPositiveButton(R.string.remove) { _, _ ->
                            coroutineScope.launch(Dispatchers.IO) {
                                viewModel.remove()
                                withContext(Dispatchers.Main) { routing.backStack.pop() }
                            }
                        }.setNegativeButton(R.string.button_cancel, null)
                        .create()
                        .show()
                }
            )
        ),
        onDismissRequest = { menuState.value = false },
        expanded = menuState.value
    )
}


@Composable
private fun MainContainer(
    routing: Routing<Route.EncounterDetail>,
    viewModel: EncounterDetailViewModel
) {
    val encounterId = routing.route.encounterId

    Box(
        Modifier.fillMaxSize().background(MaterialTheme.colors.background)
            .padding(top = 6.dp)
    ) {
        ScrollableColumn(Modifier.fillMaxWidth()) {
            DescriptionCard(viewModel)
            CombatantsCard(
                viewModel,
                onCreateRequest = { routing.backStack.push(Route.NpcCreation(encounterId)) },
                onEditRequest = {
                    routing.backStack.push(
                        Route.NpcDetail(NpcId(encounterId, it.id))
                    )
                },
                onRemoveRequest = { viewModel.removeCombatant(it.id) },
            )
        }
    }
}

@Composable
private fun DescriptionCard(viewModel: EncounterDetailViewModel) {
    CardContainer(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        CardTitle(R.string.title_description)

        val encounter = viewModel.encounter.collectAsState(null).value

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
    onEditRequest: (Npc) -> Unit,
    onRemoveRequest: (Npc) -> Unit,
) {
    CardContainer(Modifier.fillMaxWidth().padding(8.dp)) {
        CardTitle(R.string.title_npcs)

        val npcs = viewModel.npcs.collectAsState(null).value

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
    npcs: List<Npc>,
    onEditRequest: (Npc) -> Unit,
    onRemoveRequest: (Npc) -> Unit,
) {
    for (npc in npcs) {
        val alpha = if (npc.alive) ContentAlpha.high else ContentAlpha.disabled

        Providers(AmbientContentAlpha provides alpha) {
            CardItem(
                name = npc.name,
                iconRes = if (npc.alive) R.drawable.ic_npc else R.drawable.ic_dead,
                onClick = { onEditRequest(npc) },
                contextMenuItems = listOf(
                    ContextMenu.Item(stringResource(R.string.remove)) { onRemoveRequest(npc) }
                ),
            )
        }
    }
}
