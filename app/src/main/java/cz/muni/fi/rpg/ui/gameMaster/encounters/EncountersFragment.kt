package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.domain.encounters.EncounterId
import cz.muni.fi.rpg.ui.common.serializableArgument
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.ui.gameMaster.GameMasterFragmentDirections
import cz.muni.fi.rpg.ui.gameMaster.encounters.adapter.EncounterAdapter
import cz.muni.fi.rpg.viewModels.EncountersViewModel
import kotlinx.android.synthetic.main.fragment_encounters.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class EncountersFragment : Fragment(R.layout.fragment_encounters),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_PARTY_ID = "partyId"

        fun newInstance(partyId: UUID) = EncountersFragment()
            .apply {
                arguments = bundleOf(ARGUMENT_PARTY_ID to partyId)
            }
    }

    private val partyId: UUID by serializableArgument(ARGUMENT_PARTY_ID)
    private val viewModel: EncountersViewModel by viewModel { parametersOf(partyId) }

    private lateinit var encounters: MutableList<Encounter>

    private lateinit var swapping: Job

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EncounterAdapter(layoutInflater) {encounter ->
            findNavController().navigate(
                GameMasterFragmentDirections.openEncounter(
                    EncounterId(partyId = partyId, encounterId = encounter.id)
                )
            )
        }
        encounterListRecycler.adapter = adapter
        encounterListRecycler.layoutManager = LinearLayoutManager(context)

        viewModel.encounters.observe(viewLifecycleOwner) { encounters ->
            progress.toggleVisibility(false)
            mainView.toggleVisibility(true)

            this.encounters = mutableListOf()
            this.encounters.addAll(encounters)

            adapter.submitList(this.encounters)

            noEncountersText.toggleVisibility(encounters.isEmpty())
            noEncountersIcon.toggleVisibility(encounters.isEmpty())
            encounterListRecycler.toggleVisibility(encounters.isNotEmpty())
            timeline.toggleVisibility(encounters.isNotEmpty())
        }

        ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition

                    Collections.swap(encounters, fromPosition, toPosition)
                    adapter.notifyItemMoved(fromPosition, toPosition)

                    return false
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    swapping = launch {
                        viewModel.reorderEncounters(
                            (0 until encounters.size).map { encounters[it].id to it }.toMap()
                        )
                    }
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }
            }
        ).attachToRecyclerView(encounterListRecycler)

        addEncounter.setOnClickListener {
            EncounterDialog.newInstance(partyId, null).show(childFragmentManager, null)
        }
    }
}