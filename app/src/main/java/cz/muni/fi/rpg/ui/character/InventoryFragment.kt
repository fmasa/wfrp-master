package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.ui.character.adapter.InventoryAdapter
import cz.muni.fi.rpg.ui.character.inventory.ChangeArmorDialog
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.ui.common.NonScrollableLayoutManager
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.ui.common.toggleVisibility
import cz.muni.fi.rpg.ui.views.MoneyView
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class InventoryFragment : Fragment(R.layout.fragment_inventory),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = InventoryFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val characterId: CharacterId by parcelableArgument(ARGUMENT_CHARACTER_ID)
    private val viewModel: InventoryViewModel by viewModel { parametersOf(characterId) }

    private fun setEmptyCollectionView(view: View, isEmpty: Boolean) {
        view.findViewById<View>(R.id.noInventoryItemIcon).toggleVisibility(isEmpty)
        view.findViewById<View>(R.id.noInventoryItemText).toggleVisibility(isEmpty)
        view.findViewById<View>(R.id.inventoryRecycler).toggleVisibility(!isEmpty)
    }

    private fun showDialog(existingItem: InventoryItem?) {
        InventoryItemDialog.newInstance(characterId, existingItem)
            .show(childFragmentManager, "InventoryItemDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val characterMoney = view.findViewById<MoneyView>(R.id.characterMoney)
        viewModel.money.observe(viewLifecycleOwner, characterMoney::setValue)

        characterMoney.setOnClickListener {
            TransactionDialog.newInstance(characterId).show(parentFragmentManager, null)
        }

        view.findViewById<View>(R.id.addNewInventoryItemButton).setOnClickListener { showDialog(null) }

        val adapter = InventoryAdapter(
            layoutInflater,
            onClickListener = this::showDialog,
            onRemoveListener = { launch { viewModel.removeInventoryItem(it) } }
        )

        val inventoryRecycler = view.findViewById<RecyclerView>(R.id.inventoryRecycler)
        inventoryRecycler.layoutManager = NonScrollableLayoutManager(requireContext())
        inventoryRecycler.adapter = adapter

        viewModel.inventory.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            setEmptyCollectionView(view, items.isEmpty())
        }

        viewModel.armor.observe(viewLifecycleOwner) { errorOrArmor ->
            errorOrArmor.fold(
                {
                    toast("An error occurred during armor loading", Toast.LENGTH_LONG)
                    Timber.w(it)
                },
                { armor ->
                    view.findViewById<TextView>(R.id.headArmorValue).text = armor.head.toString()
                    view.findViewById<TextView>(R.id.bodyArmorValue).text = armor.body.toString()
                    view.findViewById<TextView>(R.id.leftArmArmorValue).text = armor.leftArm.toString()
                    view.findViewById<TextView>(R.id.rightArmArmorValue).text = armor.rightArm.toString()
                    view.findViewById<TextView>(R.id.leftLegArmorValue).text = armor.leftLeg.toString()
                    view.findViewById<TextView>(R.id.rightLegArmorValue).text = armor.rightLeg.toString()
                    view.findViewById<TextView>(R.id.shieldArmorValue).text = armor.shield.toString()

                    view.findViewById<View>(R.id.armorCard).setOnClickListener {
                        ChangeArmorDialog.newInstance(characterId, armor)
                            .show(childFragmentManager, null)
                    }
                }
            )
        }
    }
}
