package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.loadVectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.viewModels.EncountersViewModel

@Composable
fun EncountersScreen(
    viewModel: EncountersViewModel,
    modifier: Modifier,
    onEncounterClick: (Encounter) -> Unit,
    onNewEncounterDialogRequest: () -> Unit,
) {
    val encounters = viewModel.encounters.collectAsState(null).value

    if (encounters == null) {
        Box(modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        return
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEncounterDialogRequest) {
                loadVectorResource(R.drawable.ic_add).resource.resource?.let { Icon(it) }
            }
        }
    ) {
        EncounterList(encounters, viewModel, onEncounterClick)
    }
}

@Composable
private fun EncounterList(
    encounters: List<Encounter>,
    viewModel: EncountersViewModel,
    onClick: (Encounter) -> Unit
) {
    if (encounters.isEmpty()) {
        EmptyUI(
            textId = R.string.no_encounters_prompt,
            subTextId = R.string.no_encounters_sub_prompt,
            drawableResourceId = R.drawable.ic_encounter
        )

        return
    }

    val icon = loadVectorResource(R.drawable.ic_encounter).resource.resource
    val iconSize = 28.dp

    ScrollableColumn(
        Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(Spacing.bodyPadding),
    ) {
        DraggableListFor(
            encounters,
            itemSpacing = Spacing.small,
            onReorder = {
                viewModel.reorderEncounters(
                    it.mapIndexed { index, encounter -> encounter.id to index }.toMap()
                )
            },
        ) { _, encounter, isDragged ->
            Card(
                elevation = if (isDragged) 6.dp else 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .tapGestureFilter { onClick(encounter) }
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon?.let {
                        Image(
                            icon,
                            Modifier.size(iconSize),
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                        )
                    }
                    Text(
                        encounter.name,
                        modifier = Modifier.padding(start = 8.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}