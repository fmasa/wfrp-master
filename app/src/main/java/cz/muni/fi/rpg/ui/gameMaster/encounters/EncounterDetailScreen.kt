package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Npc
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounter.NpcId
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.composables.ContextMenu
import cz.muni.fi.rpg.ui.router.Route
import cz.muni.fi.rpg.ui.router.Routing
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

    Scaffold(
        topBar = {
            val partyId = encounterId.partyId
            val encounter = viewModel.encounter.right().collectAsState(null).value

            TopAppBar(
                title = {
                    Column {
                        encounter?.let { Text(it.name) }

                        val partyViewModel: PartyViewModel by viewModel { parametersOf(partyId) }
                        partyViewModel.party.right().collectAsState(null).value?.let {
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
    ) {
        MainContainer(routing, viewModel)
    }
}

@Composable
private fun TopAppBarActions(
    routing: Routing<Route.EncounterDetail>,
    encounter: Encounter,
    partyId: UUID,
    viewModel: EncounterDetailViewModel
) {
    val context = ContextAmbient.current
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

        val encounter = viewModel.encounter.right().collectAsState(null).value

        if (encounter == null) {
            Box(Modifier.fillMaxWidth(), gravity = Alignment.Center) {
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
            Box(Modifier.fillMaxWidth(), gravity = Alignment.Center) {
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
                alignment = Alignment.TopCenter
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
        val emphasis = AmbientEmphasisLevels.current

        ProvideEmphasis(if (npc.alive) emphasis.high else emphasis.disabled) {
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
