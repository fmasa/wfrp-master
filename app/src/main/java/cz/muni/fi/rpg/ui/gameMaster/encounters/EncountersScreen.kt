package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.loadVectorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.ui.common.composables.DraggableListFor
import cz.muni.fi.rpg.ui.common.composables.EmptyUI
import cz.muni.fi.rpg.viewModels.EncountersViewModel

@Composable
fun EncountersScreen(
    viewModel: EncountersViewModel,
    modifier: Modifier,
    onEncounterClick: (Encounter) -> Unit,
    onNewEncounterDialogRequest: () -> Unit,
) {
    val encounters = viewModel.encounters.observeAsState().value

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
    val iconSize = 40.dp
    val itemMargin = 4.dp
    val itemHeight = iconSize + 12.dp * 2

    ScrollableColumn(
        Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
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
                    icon?.let {
                        Image(
                            icon,
                            Modifier.size(iconSize),
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                        )
                    }
                    Text(
                        encounter.name,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }
}