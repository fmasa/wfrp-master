package cz.muni.fi.rpg.ui.gameMaster.encounters

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.encounter.Encounter
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.common.NonScrollableLayoutManager
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.ui.gameMaster.encounters.adapter.CombatantAdapter
import cz.muni.fi.rpg.viewModels.EncounterDetailViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class EncounterDetailFragment : PartyScopedFragment(R.layout.fragment_encounter_detail),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val args: EncounterDetailFragmentArgs by navArgs()

    private val viewModel: EncounterDetailViewModel by viewModel { parametersOf(args.encounterId) }

    private lateinit var encounter: Encounter

    override fun getPartyId(): UUID = args.encounterId.partyId

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        party.observe(viewLifecycleOwner) { party ->
            setSubtitle(party.getName())
        }

        viewModel.encounter.right().observe(viewLifecycleOwner) { encounter ->
            setTitle(encounter.name)
            view.findViewById<TextView>(R.id.encounterDescription).text = encounter.description
            setHasOptionsMenu(true)

            this.encounter = encounter
        }

        val combatantListRecycler = view.findViewById<RecyclerView>(R.id.combatantListRecycler)

        viewModel.combatants.observe(viewLifecycleOwner) { combatants ->
            if (combatants.isNotEmpty()) {
                val adapter = CombatantAdapter(
                    layoutInflater,
                    { combatant ->
                        findNavController().navigate(
                            EncounterDetailFragmentDirections
                                .openCombatantForm(
                                    encounterId = args.encounterId,
                                    combatantId = combatant.id
                                )
                        )
                    },
                    { combatant -> launch { viewModel.removeCombatant(combatant.id) } }
                )
                combatantListRecycler.adapter = adapter
                combatantListRecycler.layoutManager = NonScrollableLayoutManager(requireContext())
                adapter.submitList(combatants)
            }

            view.findViewById<View>(R.id.noCombatantsIcon).toggleVisibility(combatants.isEmpty())
            view.findViewById<View>(R.id.noCombatantsText).toggleVisibility(combatants.isEmpty())
            combatantListRecycler.toggleVisibility(combatants.isNotEmpty())

            view.findViewById<View>(R.id.addCombatantButton).setOnClickListener {
                findNavController().navigate(
                    EncounterDetailFragmentDirections.openCombatantForm(
                        encounterId = args.encounterId,
                        combatantId = null
                    )
                )
            }
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