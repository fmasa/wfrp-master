package cz.frantisekmasa.wfrp_master.common.encounters

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.DragIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.HamburgerButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.FilterBar
import cz.frantisekmasa.wfrp_master.common.encounters.domain.Encounter
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

class EncountersScreen(
    private val partyId: PartyId,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: EncountersScreenModel = rememberScreenModel(arg = partyId)

        var showCompleted by rememberSaveable { mutableStateOf(false) }

        val encounters = (
            if (showCompleted)
                screenModel.allEncounters
            else screenModel.notCompletedEncounters
            ).collectWithLifecycle(null).value

        if (encounters == null) {
            FullScreenProgress()
            return
        }

        var createDialogOpened by rememberSaveable { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { HamburgerButton() },
                    title = { Text(LocalStrings.current.encounters.title) }
                )
            },
            floatingActionButton = {
                AddEncounterButton(
                    onCreateEncounterRequest = { createDialogOpened = true },
                )
            }
        ) {

            if (createDialogOpened) {
                EncounterDialog(
                    existingEncounter = null,
                    screenModel = screenModel,
                    onDismissRequest = { createDialogOpened = false }
                )
            }

            val navigation = LocalNavigationTransaction.current

            Column {
                FilterBar {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        CheckboxWithText(
                            checked = showCompleted,
                            text = LocalStrings.current.encounters.buttonShowCompleted,
                            onCheckedChange = { showCompleted = !showCompleted },
                        )
                    }
                }

                val onClick: (Encounter) -> Unit = {
                    navigation.navigate(EncounterDetailScreen(EncounterId(partyId, it.id)))
                }
                if (showCompleted) {
                    AllEncountersList(encounters, onClick)
                } else {
                    ActiveEncounterList(
                        encounters,
                        screenModel,
                        onClick = onClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddEncounterButton(onCreateEncounterRequest: () -> Unit) {
    FloatingActionButton(onClick = onCreateEncounterRequest) {
        Icon(Icons.Rounded.Add, LocalStrings.current.encounters.buttonAdd)
    }
}

@Composable
private fun AllEncountersList(encounters: List<Encounter>, onClick: (Encounter) -> Unit) {
    LazyColumn {
        items(encounters) { encounter ->
            EncounterItem(
                encounter,
                onClick,
                draggable = false,
            )
        }
    }
}

@Composable
fun EncounterItem(
    encounter: Encounter,
    onClick: (Encounter) -> Unit,
    draggable: Boolean,
) {
    Column {
        ListItem(
            modifier = Modifier.clickable { onClick(encounter) },
            icon = {
                if (draggable) {
                    Icon(
                        Icons.Rounded.DragIndicator,
                        VisualOnlyIconDescription,
                        modifier = Modifier.height(
                            with(ItemIcon.Size.Small) { dimensions + padding * 2 }
                        )
                    )
                }
            },
            text = {
                Text(encounter.name)
            },
            secondaryText = {
                val npcCount = remember(encounter.characters) { encounter.characters.values.sum() }
                Text("$npcCount ${LocalStrings.current.npcs.titlePlural}")
            },
            trailing = {
                if (encounter.completed) {
                    Icon(
                        Icons.Rounded.Done,
                        contentDescription = LocalStrings.current.encounters.labelCompleted,
                    )
                }
            }
        )
        Divider()
    }
}

@Composable
private fun ActiveEncounterList(
    encounters: List<Encounter>,
    screenModel: EncountersScreenModel,
    onClick: (Encounter) -> Unit,
) {
    if (encounters.isEmpty()) {
        val strings = LocalStrings.current.encounters
        EmptyUI(
            text = strings.messages.noEncounters,
            subText = strings.messages.noEncountersSubtext,
            icon = Resources.Drawable.Encounter,
        )

        return
    }

    Column(
        Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        DraggableListFor(
            encounters,
            modifier = Modifier.fillMaxHeight(),
            onReorder = {
                screenModel.reorderEncounters(
                    it.mapIndexed { index, encounter -> encounter.id to index }.toMap()
                )
            },
        ) { _, encounter, isDragged ->
            Surface(
                color = if (isDragged)
                    MaterialTheme.colors.surface
                else Color.Transparent,
                elevation = if (isDragged) 1.dp else 0.dp,
            ) {
                EncounterItem(
                    encounter,
                    onClick = onClick,
                    draggable = true,
                )
            }
        }
    }
}
