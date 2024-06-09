package cz.frantisekmasa.wfrp_master.common.encounters

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.combat.ActiveCombatScreen
import cz.frantisekmasa.wfrp_master.common.combat.StartCombatDialog
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.CharacterAvatar
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.PrimaryButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardItem
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.AlertDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.DropdownMenuItem
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VISUAL_ONLY_ICON_DESCRIPTION
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.OptionsAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.npcs.NpcsScreenModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

class EncounterDetailScreen(
    private val encounterId: EncounterId,
) : Screen {
    @Composable
    override fun Content() {
        val screenModel: EncounterDetailScreenModel = rememberScreenModel(arg = encounterId)
        val npcsScreenModel: NpcsScreenModel = rememberScreenModel(arg = encounterId.partyId)

        var startCombatDialogVisible by rememberSaveable { mutableStateOf(false) }
        val encounter = screenModel.encounter.collectWithLifecycle(null).value

        Scaffold(
            topBar = {
                val partyId = encounterId.partyId

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
                    },
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    icon = {
                        Icon(
                            drawableResource(Resources.Drawable.Encounter),
                            VISUAL_ONLY_ICON_DESCRIPTION,
                            Modifier.width(24.dp),
                        )
                    },
                    text = { Text(stringResource(Str.combat_title_start_combat)) },
                    onClick = { startCombatDialogVisible = true },
                )
            },
            content = {
                if (encounter == null) {
                    FullScreenProgress()
                    return@Scaffold
                }

                MainContainer(encounter, screenModel, npcsScreenModel)

                if (startCombatDialogVisible) {
                    val navigation = LocalNavigationTransaction.current

                    StartCombatDialog(
                        encounter = encounter,
                        onDismissRequest = { startCombatDialogVisible = false },
                        screenModel = rememberScreenModel(arg = encounterId.partyId),
                        onComplete = {
                            startCombatDialogVisible = false
                            navigation.navigate(ActiveCombatScreen(encounterId.partyId))
                        },
                    )
                }
            },
        )
    }

    @Composable
    private fun TopAppBarActions(
        encounter: Encounter,
        screenModel: EncounterDetailScreenModel,
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

        IconButton(onClick = { editDialogOpened = true }) {
            Icon(
                Icons.Rounded.Edit,
                stringResource(Str.encounters_title_edit),
                tint = contentColorFor(MaterialTheme.colors.primarySurface),
            )
        }

        OptionsAction {
            val navigation = LocalNavigationTransaction.current

            var dialogOpened by remember { mutableStateOf(false) }

            if (dialogOpened) {
                AlertDialog(
                    onDismissRequest = { dialogOpened = false },
                    text = { Text(stringResource(Str.encounters_messages_removal_confirmation)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                coroutineScope.launch(Dispatchers.IO) {
                                    screenModel.remove()
                                    navigation.goBack()
                                }
                            },
                        ) {
                            Text(stringResource(Str.common_ui_button_remove).uppercase())
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { dialogOpened = false }) {
                            Text(stringResource(Str.common_ui_button_cancel).uppercase())
                        }
                    },
                )
            }

            DropdownMenuItem(
                onClick = { dialogOpened = true },
            ) {
                Text(stringResource(Str.common_ui_button_remove))
            }
        }
    }

    @Composable
    private fun MainContainer(
        encounter: Encounter,
        screenModel: EncounterDetailScreenModel,
        npcsScreenModel: NpcsScreenModel,
    ) {
        val coroutineScope = rememberCoroutineScope { EmptyCoroutineContext + Dispatchers.IO }

        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .verticalScroll(rememberScrollState())
                .padding(bottom = Spacing.bottomPaddingUnderFab),
        ) {
            SubheadBar {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(Str.encounters_label_completed))
                    Switch(
                        checked = encounter.completed,
                        onCheckedChange = {
                            coroutineScope.launch(Dispatchers.IO) {
                                screenModel.updateEncounter(encounter.copy(completed = it))
                            }
                        },
                    )
                }
            }

            Spacer(Modifier.height(Spacing.small))

            DescriptionCard(screenModel)

            NpcCharacterList(encounterId.partyId, encounter, screenModel, npcsScreenModel)
        }
    }

    @Composable
    private fun DescriptionCard(screenModel: EncounterDetailScreenModel) {
        CardContainer(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
        ) {
            CardTitle(stringResource(Str.encounters_title_description))

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
}

@Composable
private fun NpcCharacterList(
    partyId: PartyId,
    encounter: Encounter,
    screenModel: EncounterDetailScreenModel,
    npcsScreenModel: NpcsScreenModel,
) {
    CardContainer(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        CardTitle(stringResource(Str.npcs_title_plural))

        val npcs = screenModel.npcsInEncounter.collectWithLifecycle(null).value

        if (npcs == null) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@CardContainer
        }

        if (npcs.isEmpty()) {
            EmptyUI(
                text = stringResource(Str.npcs_messages_no_npcs),
                icon = Resources.Drawable.Npc,
                size = EmptyUI.Size.Small,
            )
        }

        val navigation = LocalNavigationTransaction.current
        val coroutineScope = rememberCoroutineScope()

        npcs.forEach { npc ->
            key(npc.id) {
                NpcItem(
                    npc = npc,
                    count = npc.count,
                    onDetailRequest = {
                        navigation.navigate(CharacterDetailScreen(CharacterId(partyId, npc.id)))
                    },
                    onCountChange = { count ->
                        coroutineScope.launch(Dispatchers.IO) {
                            screenModel.updateEncounter(encounter.withCharacterCount(npc.id, count))
                        }
                    },
                )
            }
        }

        var chooseNpcDialogVisible by rememberSaveable { mutableStateOf(false) }

        if (chooseNpcDialogVisible) {
            ChooseNpcDialog(
                screenModel = screenModel,
                npcsScreenModel = npcsScreenModel,
                onDismissRequest = { chooseNpcDialogVisible = false },
            )
        }

        Box(
            Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            PrimaryButton(
                stringResource(Str.npcs_button_add_npc),
                onClick = { chooseNpcDialogVisible = true },
            )
        }
    }
}

@Composable
private fun NpcItem(
    npc: NpcInEncounter,
    count: Int,
    onDetailRequest: () -> Unit,
    onCountChange: (Int) -> Unit,
) {
    CardItem(
        name = npc.name,
        icon = {
            CharacterAvatar(npc.avatarUrl, ItemIcon.Size.Small, fallback = Resources.Drawable.Npc)
        },
        onClick = onDetailRequest,
        badge = {
            NumberPicker(
                value = count,
                onIncrement = { onCountChange(count + 1) },
                onDecrement = { onCountChange((count - 1).coerceAtLeast(0)) },
            )
        },
        contextMenuItems =
            listOf(
                ContextMenu.Item(
                    text = stringResource(Str.common_ui_button_open),
                    onClick = onDetailRequest,
                ),
            ),
    )
}

data class NpcInEncounter(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val count: Int,
)
