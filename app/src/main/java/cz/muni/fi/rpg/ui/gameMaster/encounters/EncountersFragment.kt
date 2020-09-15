package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.ui.common.composables.DraggableListFor
import cz.muni.fi.rpg.ui.common.composables.Theme
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.gameMaster.GameMasterFragmentDirections
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class EncountersFragment : Fragment() {
    companion object {
        private const val ARGUMENT_PARTY_ID = "partyId"

        fun newInstance(partyId: UUID) = EncountersFragment()
            .apply {
                arguments = bundleOf(ARGUMENT_PARTY_ID to partyId)
            }
    }

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Theme {
                EncountersScreen(
                    viewModel,
                    onEncounterClick = {
                        findNavController().navigate(
                            GameMasterFragmentDirections.openEncounter(
                                EncounterId(partyId = partyId, encounterId = it.id)
                            )
                        )
                    },
                    onNewEncounterDialogRequest = {
                        EncounterDialog.newInstance(partyId, null).show(childFragmentManager, null)
                    }
                )
            }
        }
    }
}

@Composable
private fun EncountersScreen(
    viewModel: EncountersViewModel,
    onEncounterClick: (Encounter) -> Unit,
    onNewEncounterDialogRequest: () -> Unit,
) {
    Scaffold(
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
            horizontalGravity = Alignment.CenterHorizontally,
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
            .fillMaxWidth(0.7f)
            .padding(top = 6.dp),
        itemHeight = itemHeight + itemMargin * 2,
        onReorder = {
            viewModel.reorderEncounters(
                it.mapIndexed { index, encounter -> encounter.id to index }.toMap()
            )
        },
    ) { encounter, isDragged ->
        Card(
            elevation = if (isDragged) 6.dp else 2.dp,
            modifier = Modifier
                .padding(itemMargin)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .clickable(onClick = { onClick(encounter) })
        ) {
            Row(
                Modifier.height(itemHeight).padding(12.dp),
                verticalGravity = Alignment.CenterVertically
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