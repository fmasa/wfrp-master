package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_inventory.*

class InventoryFragment : DaggerFragment(R.layout.fragment_inventory) {
    private val viewModel: CharacterViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Transformations.map(viewModel.character.right()) { character -> character.getMoney() }
            .observe(viewLifecycleOwner, characterMoney::setValue)
    }
}
