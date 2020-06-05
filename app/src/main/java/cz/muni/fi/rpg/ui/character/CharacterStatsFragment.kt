package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.viewModels.CharacterStatsViewModel
import kotlinx.android.synthetic.main.fragment_character_stats.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CharacterStatsFragment : Fragment(R.layout.fragment_character_stats) {
    companion object {
        private const val ARGUMENT_CHARACTER_ID = "CHARACTER_ID"

        fun newInstance(characterId: CharacterId) = CharacterStatsFragment().apply {
            arguments = bundleOf(ARGUMENT_CHARACTER_ID to characterId)
        }
    }

    private val viewModel: CharacterStatsViewModel by viewModel {
        parametersOf(arguments?.getParcelable(ARGUMENT_CHARACTER_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.character.right().observe(viewLifecycleOwner) { character ->
            raceAndCareer.text = StringBuilder()
                .append(getString(character.getRace().getReadableNameId()))
                .append(" ")
                .append(character.getCareer())
                .toString()
        }

        bindStats()
        bindPoints()
    }

    private fun bindStats() {
        Transformations.map(viewModel.character.right()) { character -> character.getStats() }
            .observe(viewLifecycleOwner) { stats ->
                weaponSkill.value = stats.weaponSkill
                ballisticSkill.value = stats.ballisticSkill

                strength.value = stats.strength
                strengthBonus.value = stats.strengthBonus

                toughness.value = stats.toughness
                toughnessBonus.value = stats.toughnessBonus

                agility.value = stats.agility
                intelligence.value = stats.intelligence
                willPower.value = stats.willPower
                fellowship.value = stats.fellowship
                magic.value = stats.magic
            }
    }

    private fun bindPoints() {
        Transformations
            .map(viewModel.character.right()) { character -> character.getPoints() }
            .observe(viewLifecycleOwner) { points ->
                wounds.value = points.wounds
                wounds.setColor(if (points.isHeavilyWounded()) R.color.colorDanger else R.color.colorText)

                fortunePoints.value = points.fortune
                fatePoints.value = points.fate
                insanityPoints.value = points.insanity
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
