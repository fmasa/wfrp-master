package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.ui.common.composables.DraggableListFor
import cz.muni.fi.rpg.viewModels.EncountersViewModel

@Composable
fun EncountersScreen(
    viewModel: EncountersViewModel,
    modifier: Modifier,
    onEncounterClick: (Encounter) -> Unit,
    onNewEncounterDialogRequest: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onNewEncounterDialogRequest) {
                Icon(vectorResource(R.drawable.ic_add))
            }
        }
    ) {
        ScrollableColumn(
            Modifier
                .background(MaterialTheme.colors.background)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) { EncounterList(viewModel, onEncounterClick) }
    }
}

@Composable
private fun EncounterList(viewModel: EncountersViewModel, onClick: (Encounter) -> Unit) {
    val encounters = viewModel.encounters.observeAsState().value ?: return

    val icon = vectorResource(R.drawable.ic_encounter)

    val itemMargin = 4.dp
    val iconSize = 40.dp
    val itemHeight = iconSize + 12.dp * 2

    DraggableListFor(
        encounters,
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .padding(top = 6.dp),
        itemHeight = itemHeight + itemMargin * 2,
        onReorder = {
            viewModel.reorderEncounters(
                it.mapIndexed { index, encounter -> encounter.id to index }.toMap()
            )
        },
    ) { encounter, isDragged, modifier ->
        Card(
            elevation = if (isDragged) 6.dp else 2.dp,
            modifier = Modifier
                .padding(itemMargin)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clickable(onClick = { onClick(encounter) })
                .then(modifier)
        ) {
            Row(
                Modifier.height(itemHeight).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    icon,
                    Modifier.size(iconSize),
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                )
                Text(
                    encounter.name,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body1
                )
            }
        }
    }
}