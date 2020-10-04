package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Npc
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.composables.*
import cz.muni.fi.rpg.ui.common.composables.ContextMenu
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class EncounterDetailFragment : PartyScopedFragment(0),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val args: EncounterDetailFragmentArgs by navArgs()

    private val viewModel: EncounterDetailViewModel by viewModel { parametersOf(args.encounterId) }

    private lateinit var encounter: Encounter

    override fun getPartyId(): UUID = args.encounterId.partyId

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            Theme {
                EncounterDetailScreen(
                    viewModel,
                    openCombatantDetail = {
                        findNavController().navigate(
                            EncounterDetailFragmentDirections.openCombatantForm(
                                encounterId = args.encounterId,
                                npcId = it?.id
                            )
                        )
                    },
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        party.observe(viewLifecycleOwner) { party ->
            setSubtitle(party.getName())
        }

        viewModel.encounter.right().observe(viewLifecycleOwner) { encounter ->
            setTitle(encounter.name)
            setHasOptionsMenu(true)

            this.encounter = encounter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.encounter_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionEdit -> {
                EncounterDialog.newInstance(
                    args.encounterId.partyId,
                    EncounterDialog.Defaults(encounter.id, encounter.name, encounter.description)
                ).show(childFragmentManager, null)
            }
            R.id.actionRemove -> {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.question_remove_encounter)
                    .setPositiveButton(R.string.remove) { _, _ ->
                        launch {
                            viewModel.remove()
                            withContext(Dispatchers.Main) { findNavController().popBackStack() }
                        }
                    }.setNegativeButton(R.string.button_cancel, null)
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}

@Composable
private fun EncounterDetailScreen(
    viewModel: EncounterDetailViewModel,
    openCombatantDetail: (Npc?) -> Unit,
) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colors.background).padding(top = 6.dp)) {
        ScrollableColumn(Modifier.fillMaxWidth()) {
            DescriptionCard(viewModel)
            CombatantsCard(
                viewModel,
                onCreateRequest = { openCombatantDetail(null) },
                onEditRequest = openCombatantDetail,
                onRemoveRequest = { viewModel.removeCombatant(it.id) },
            )
        }
    }
}

@Composable
private fun DescriptionCard(viewModel: EncounterDetailViewModel) {
    CardContainer(Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        CardTitle(R.string.title_description)

        val encounter = viewModel.encounter.right().observeAsState().value

        if (encounter == null) {
            Box(Modifier.fillMaxWidth(), gravity = ContentGravity.Center) {
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

        val npcs = viewModel.npcs.observeAsState().value

        if (npcs == null) {
            Box(Modifier.fillMaxWidth(), gravity = ContentGravity.Center) {
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
        val emphasis = EmphasisAmbient.current

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