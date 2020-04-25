package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Transformations
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.CharacterViewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_character_stats.*
import java.util.*
import javax.inject.Inject

class CharacterStatsFragment : DaggerFragment(R.layout.fragment_character_stats) {
    companion object {
        const val ARG_PARTY_ID = "pointFragment_partyId"
        const val ARG_USER_ID = "pointFragment_userId"
    }

    @Inject
    lateinit var viewModelFactory: CharacterViewModelProvider

    private val viewModel: CharacterViewModel by activityViewModels {
        viewModelFactory.factory(
            arguments?.getString(ARG_PARTY_ID)?.let(UUID::fromString)
                ?: error("Party ID must be set!"),
            arguments?.getString(ARG_USER_ID) ?: error("User ID must be set!")
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindPoints()
    }

    private fun bindPoints() {
        Transformations
            .map(viewModel.character.right()) { character -> character.points }
            .observe(viewLifecycleOwner) { points ->
                wounds.value = points.wounds
                fortunePoints.value = points.fortune
                fatePoints.value = points.fate
                insanityPoints.value = points.insanity
            }

        Transformations.map(viewModel.character.right()) { character -> character.race }
            .observe(viewLifecycleOwner) {
                race.text = getString(it.getReadableNameId())
            }

        wounds.setIncrementListener(viewModel::incrementWounds)
        wounds.setDecrementListener(viewModel::decrementWounds)

        fortunePoints.setIncrementListener(viewModel::incrementFortunePoints)
        fortunePoints.setDecrementListener(viewModel::decrementFortunePoints)

        fatePoints.setIncrementListener(viewModel::incrementFatePoints)
        fatePoints.setDecrementListener(viewModel::decrementFatePoints)

        insanityPoints.setIncrementListener(viewModel::incrementInsanityPoints)
        insanityPoints.setDecrementListener(viewModel::decrementInsanityPoints)
    }
}
