package cz.frantisekmasa.wfrp_master.common.encounters

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
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.combat.ActiveCombatScreen
import cz.frantisekmasa.wfrp_master.common.combat.StartCombatDialog
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.NpcId
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.AlertDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.common.npcs.NpcCreationScreen
import cz.frantisekmasa.wfrp_master.common.npcs.NpcDetailScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

class EncounterDetailScreen(
    private val encounterId: EncounterId
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: EncounterDetailScreenModel = rememberScreenModel(arg = encounterId)

        var startCombatDialogVisible by rememberSaveable { mutableStateOf(false) }

        Scaffold(
            topBar = {
                val partyId = encounterId.partyId
                val encounter = screenModel.encounter.collectWithLifecycle(null).value

                TopAppBar(
                    title = {
                        Column {
                            encounter?.let { Text(it.name) }

                            val partyViewModel: PartyScreenModel = rememberScreenModel(arg = partyId)
                            partyViewModel.party.collectWithLifecycle(null).value?.let {
                                Subtitle(it.name)
                            }
                        }
                    },
                    navigationIcon = { BackButton() },
                    actions = {
                        encounter?.let {
                            TopAppBarActions(
                                encounter = it,
                                screenModel = screenModel,
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
                            drawableResource(Resources.Drawable.Encounter),
                            VisualOnlyIconDescription,
                            Modifier.width(24.dp),
                        )
                    },
                    text = { Text(LocalStrings.current.combat.titleStartCombat) },
                    onClick = { startCombatDialogVisible = true },
                )
            },
            content = {
                MainContainer(screenModel)

                if (startCombatDialogVisible) {
                    val navigator = LocalNavigator.currentOrThrow

                    StartCombatDialog(
                        encounterId = encounterId,
                        onDismissRequest = { startCombatDialogVisible = false },
                        screenModel = rememberScreenModel(arg = encounterId.partyId),
                        onComplete = {
                            startCombatDialogVisible = false
                            navigator.push(ActiveCombatScreen(encounterId.partyId))
                        },
                    )
                }
            }
        )
    }

    @Composable
    private fun TopAppBarActions(
        encounter: Encounter,
        screenModel: EncounterDetailScreenModel
    ) {
        val coroutineScope = rememberCoroutineScope()

        var editDialogOpened by rememberSaveable { mutableStateOf(false) }

        if (editDialogOpened) {
            EncounterDialog(
                existingEncounter = encounter,
                screenModel = rememberScreenModel(arg = encounterId.partyId),
                onDismissRequest = { editDialogOpened = false },
            )
        }

        val strings = LocalStrings.current

        IconButton(onClick = { editDialogOpened = true }) {
            Icon(
                Icons.Rounded.Edit,
                strings.encounters.titleEdit,
                tint = contentColorFor(MaterialTheme.colors.primarySurface),
            )
        }

        OptionsAction {
            val navigator = LocalNavigator.currentOrThrow

            var dialogOpened by remember { mutableStateOf(false) }

            if (dialogOpened) {
                AlertDialog(
                    onDismissRequest = { dialogOpened = false },
                    text = { Text(strings.encounters.messages.removalConfirmation) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    screenModel.remove()
                                    navigator.pop()
                                }
                            }
                        ) {
                            Text(strings.commonUi.buttonRemove)
                        }
                    },
                    dismissButton = { dialogOpened = false }
                )
            }

            DropdownMenuItem(
                onClick = { dialogOpened = true }
            ) {
                Text(LocalStrings.current.commonUi.buttonRemove)
            }
        }
    }

    @Composable
    private fun MainContainer(
        screenModel: EncounterDetailScreenModel
    ) {
        val coroutineScope = rememberCoroutineScope { EmptyCoroutineContext + Dispatchers.IO }

        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .padding(top = 6.dp, bottom = Spacing.bottomPaddingUnderFab),
        ) {
            DescriptionCard(screenModel)

            val navigator = LocalNavigator.currentOrThrow

            NpcsCard(
                screenModel,
                onCreateRequest = { navigator.push(NpcCreationScreen(encounterId)) },
                onEditRequest = { navigator.push(NpcDetailScreen(it)) },
                onRemoveRequest = { screenModel.removeNpc(it) },
                onDuplicateRequest = { coroutineScope.launch { screenModel.duplicateNpc(it.npcId) } }
            )
        }
    }

    @Composable
    private fun DescriptionCard(screenModel: EncounterDetailScreenModel) {
        CardContainer(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            CardTitle(LocalStrings.current.encounters.titleDescription)

            val encounter = screenModel.encounter.collectWithLifecycle(null).value

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
    private fun NpcsCard(
        screenModel: EncounterDetailScreenModel,
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
            val strings = LocalStrings.current.npcs

            CardTitle(strings.titlePlural)

            val npcs = screenModel.npcs.collectWithLifecycle(null).value

            if (npcs == null) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                return@CardContainer
            }

            Column(Modifier.fillMaxWidth()) {

                if (npcs.isEmpty()) {
                    EmptyUI(
                        text = strings.messages.noNpcs,
                        icon = Resources.Drawable.Npc,
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
                    PrimaryButton(LocalStrings.current.npcs.buttonAddNpc, onClick = onCreateRequest)
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
                    icon = {
                        ItemIcon(
                            if (npc.alive)
                                Resources.Drawable.Npc
                            else Resources.Drawable.Dead,
                            ItemIcon.Size.Small
                        )
                    },
                    onClick = { onEditRequest(npc.id) },
                    contextMenuItems = listOf(
                        ContextMenu.Item(
                            text = LocalStrings.current.commonUi.buttonDuplicate,
                            onClick = { onDuplicateRequest(npc.id) },
                        ),
                        ContextMenu.Item(
                            text = LocalStrings.current.commonUi.buttonRemove,
                            onClick = { onRemoveRequest(npc.id) },
                        ),
                    ),
                )
            }
        }
    }

}
