package cz.muni.fi.rpg.ui.character

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations
import androidx.lifecycle.observe
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.character.Points
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.views.CharacterPoint
import cz.muni.fi.rpg.viewModels.CharacterStatsViewModel
import kotlinx.android.synthetic.main.dialog_xp.view.*
import kotlinx.android.synthetic.main.fragment_character_stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CharacterStatsFragment : Fragment(R.layout.fragment_character_stats),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
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

        bindTopBar()
        bindStats()
        bindPoints()
    }

    private fun bindTopBar() {
        viewModel.character.right().observe(viewLifecycleOwner) { character ->
            raceAndCareer.text = StringBuilder()
                .append(getString(character.getRace().getReadableNameId()))
                .append(" ")
                .append(character.getCareer())
                .toString()

            xpPoints.text = getString(R.string.xp_points, character.getPoints().experience)

            xpPoints.setOnClickListener {
                openExperiencePointsDialog(character.getPoints().experience)
            }
        }
    }

    private fun bindStats() {
        Transformations.map(viewModel.character.right()) { character -> character.getStats() }
            .observe(viewLifecycleOwner) { stats ->
                weaponSkill.value = stats.weaponSkill
                ballisticSkill.value = stats.ballisticSkill

                strength.value = stats.strength
//                strengthBonus.value = stats.strengthBonus

                toughness.value = stats.toughness
//                toughnessBonus.value = stats.toughnessBonus

                agility.value = stats.agility
                intelligence.value = stats.intelligence
                willPower.value = stats.willPower
                fellowship.value = stats.fellowship
            }
    }

    private fun bindPoints() {
        Transformations
            .map(viewModel.character.right()) { character -> character.getPoints() }
            .observe(viewLifecycleOwner) { points ->
                wounds.value = points.wounds
                wounds.setColor(if (points.isHeavilyWounded()) R.color.colorDanger else R.color.colorText)

                corruptionPoints.value = points.corruption
                sinPoints.value = points.sin
                resolvePoints.value = points.resolve
                resiliencePoints.value = points.resilience
                fortunePoints.value = points.fortune
                fatePoints.value = points.fate
            }

        val points = mapOf<CharacterPoint, (p: Points, addition: Int) -> Points>(
            wounds to { p, addition -> p.copy(wounds = p.wounds + addition)},
            corruptionPoints to {p, addition -> p.copy(corruption = p.corruption + addition)},
            sinPoints to {p, addition -> p.copy(sin = p.sin + addition)},
            fortunePoints to { p, addition -> p.copy(fortune = p.fortune + addition)},
            fatePoints to { p, addition -> p.updateFate(p.fate + addition)},
            resolvePoints to {p, addition -> p.copy(resolve = p.resolve + addition)},
            resiliencePoints to {p, addition -> p.updateResilience(p.resilience + addition)}
        )

        points.forEach {
            val pointsView = it.key
            val mutator = it.value

            pointsView.setIncrementListener {
                viewModel.updatePoints { points -> mutator(points, 1) }
            }

            pointsView.setDecrementListener {
                viewModel.updatePoints { points -> mutator(points, -1) }
            }
        }
    }

    private fun openExperiencePointsDialog(currentXpPoints: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_xp, null, false)

        view.xpPointsInput.setText(currentXpPoints.toString())

        AlertDialog.Builder(requireContext())
            .setTitle("Change amount of XP")
            .setView(view)
            .setPositiveButton(R.string.button_save) { _, _ ->
                val xpPoints = view.xpPointsInput.text.toString().toIntOrNull() ?: 0
                launch {
                    viewModel.updateExperiencePoints(xpPoints)
                }
            }.create()
            .show()
    }
}
