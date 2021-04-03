package cz.muni.fi.rpg.ui.gameMaster.encounters

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.combat.domain.encounter.Encounter
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.ui.primitives.DraggableListFor
import cz.frantisekmasa.wfrp_master.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.core.viewModel.PremiumViewModel
import cz.frantisekmasa.wfrp_master.core.viewModel.providePremiumViewModel
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.ui.common.BuyPremiumPrompt
import cz.muni.fi.rpg.viewModels.EncountersViewModel

@Composable
fun EncountersScreen(
    partyId: PartyId,
    viewModel: EncountersViewModel,
    modifier: Modifier,
    onEncounterClick: (Encounter) -> Unit,
) {
    val encounters = viewModel.encounters.observeAsState().value

    if (encounters == null) {
        Box(modifier.fillMaxSize()) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
        return
    }

    var createDialogOpened by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            AddEncounterButton(
                encounterCount = encounters.size,
                onCreateEncounterRequest = { createDialogOpened = true },
            )
        }
    ) {

        if (createDialogOpened) {
            EncounterDialog(
                existingEncounter = null,
                partyId = partyId,
                onDismissRequest = { createDialogOpened = false }
            )
        }

        EncounterList(encounters, viewModel, onEncounterClick)
    }
}

@Composable
private fun AddEncounterButton(encounterCount: Int, onCreateEncounterRequest: () -> Unit) {
    val premiumViewModel = providePremiumViewModel()

    if (encounterCount >= PremiumViewModel.FREE_ENCOUNTER_COUNT && premiumViewModel.active != true) {
        var premiumPromptVisible by remember { mutableStateOf(false) }

        FloatingActionButton(onClick = { premiumPromptVisible = true }) {
            Icon(painterResource(R.drawable.ic_premium), stringResource(R.string.buy_premium))
        }

        if (premiumPromptVisible) {
            BuyPremiumPrompt(onDismissRequest = { premiumPromptVisible = false })
        }

        return
    }

    FloatingActionButton(onClick = onCreateEncounterRequest) {
        Icon(painterResource(R.drawable.ic_add), stringResource(R.string.icon_add_encounter))
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

    val icon = painterResource(R.drawable.ic_encounter)
    val iconSize = 28.dp

    Column(
        Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.bodyPaddingWithFab),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DraggableListFor(
            encounters,
            modifier = Modifier.fillMaxHeight(),
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
                    .clickable { onClick(encounter) }
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        icon,
                        VisualOnlyIconDescription,
                        Modifier.size(iconSize),
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface)
                    )
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