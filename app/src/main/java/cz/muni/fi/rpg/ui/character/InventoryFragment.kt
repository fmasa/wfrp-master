package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import kotlinx.android.synthetic.main.fragment_inventory.view.*
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.inventory.InventoryItem
import cz.muni.fi.rpg.ui.character.adapter.InventoryAdapter
import cz.muni.fi.rpg.ui.character.inventory.ChangeArmorDialog
import cz.muni.fi.rpg.ui.character.inventory.InventoryItemDialog
import cz.muni.fi.rpg.ui.character.inventory.TransactionDialog
import cz.muni.fi.rpg.ui.common.parcelableArgument
import cz.muni.fi.rpg.viewModels.InventoryViewModel
import kotlinx.android.synthetic.main.fragment_inventory.*
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

    private fun setViewVisibility(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setEmptyCollectionView(isEmpty: Boolean) {
        setViewVisibility(noInventoryItemIcon, isEmpty)
        setViewVisibility(noInventoryItemText, isEmpty)
        setViewVisibility(inventoryRecycler, !isEmpty)
    }

    private fun showDialog(existingItem: InventoryItem?) {
        InventoryItemDialog.newInstance(characterId, existingItem)
            .show(childFragmentManager, "InventoryItemDialog")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.money.observe(viewLifecycleOwner, characterMoney::setValue)

        characterMoney.setOnClickListener {
            TransactionDialog.newInstance(characterId).show(parentFragmentManager, null)
        }

        view.addNewInventoryItemButton.setOnClickListener { showDialog(null) }

        val adapter = InventoryAdapter(
            layoutInflater,
            onClickListener = this::showDialog,
            onRemoveListener = { launch { viewModel.removeInventoryItem(it) } }
        )
        inventoryRecycler.layoutManager = LinearLayoutManager(context)
        inventoryRecycler.adapter = adapter

        viewModel.inventory.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            setEmptyCollectionView(items.isEmpty())
        }

        viewModel.armor.observe(viewLifecycleOwner) { errorOrArmor ->
            errorOrArmor.fold(
                {
                    Toast.makeText(
                        context,
                        "An error occurred during armor loading",
                        Toast.LENGTH_LONG
                    ).show()
                    Timber.w(it)
                },
                { armor ->
                    headArmorValue.text = armor.head.toString()
                    bodyArmorValue.text = armor.body.toString()
                    leftArmArmorValue.text = armor.leftArm.toString()
                    rightArmArmorValue.text = armor.rightArm.toString()
                    leftLegArmorValue.text = armor.leftLeg.toString()
                    rightLegArmorValue.text = armor.rightLeg.toString()
                    shieldArmorValue.text = armor.shield.toString()

                    armorCard.setOnClickListener {
                        ChangeArmorDialog.newInstance(characterId, armor)
                            .show(childFragmentManager, null)
                    }
                }
            )
        }
    }
}
