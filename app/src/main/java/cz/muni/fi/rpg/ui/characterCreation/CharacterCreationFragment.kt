package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.*
import cz.muni.fi.rpg.ui.common.PartyScopedFragment
import cz.muni.fi.rpg.ui.common.StaticFragmentsViewPagerAdapter
import cz.muni.fi.rpg.ui.common.toast
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import cz.muni.fi.rpg.viewModels.CharacterCreationViewModel
import kotlinx.android.synthetic.main.fragment_character_creation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.IllegalArgumentException
import java.util.*

class CharacterCreationFragment(
    private val characters: CharacterRepository
) : PartyScopedFragment(R.layout.fragment_character_creation),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val args: CharacterCreationFragmentArgs by navArgs()
    private val authentication: AuthenticationViewModel by sharedViewModel()

    private val labels = arrayOf(
        R.string.title_character_creation_info,
        R.string.title_character_stats,
        R.string.title_character_creation_points
    )

    private val fragmentFactories = arrayOf<() -> Fragment>(
        { CharacterInfoFormFragment() },
        { CharacterStatsFormFragment() },
        { CharacterPointsFormFragment() }
    )

    private val viewModel: CharacterCreationViewModel by viewModel {
        parametersOf(args.partyId)
    }

    private var currentFragmentIndex = 0

    private var characterInfo: CharacterInfoFormFragment.Data? = null
    private var characterStatsData: CharacterStatsFormFragment.CharacteristicsData? = null

    override fun onStart() {
        super.onStart()

        launch {
            val userId = args.userId
            if (userId != null && characters.hasCharacterInParty(userId, args.partyId)) {
                withContext(Dispatchers.Main) { toast(R.string.already_has_character) }
                return@launch
            }

            withContext(Dispatchers.Main) { showStep(0) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wizardPager.isUserInputEnabled = false
        wizardPager.adapter = StaticFragmentsViewPagerAdapter(
            this,
            arrayOf(
                { CharacterInfoFormFragment() },
                { CharacterStatsFormFragment() },
                { CharacterPointsFormFragment() }
            )
        )

        buttonNext.setOnClickListener {
            when (val currentFragment = currentStep()) {
                is CharacterInfoFormFragment -> {
                    characterInfo = currentFragment.submit() ?: return@setOnClickListener
                }
                is CharacterStatsFormFragment -> {
                    val characterInfo = this.characterInfo
                    val data = currentFragment.submit()

                    if (characterInfo == null || data == null) {
                        return@setOnClickListener
                    }

                    characterStatsData = data
                }
                is CharacterPointsFormFragment -> {
                    val info = characterInfo
                    val statsData = characterStatsData
                    val points = currentFragment.submit()

                    if (info == null || statsData == null || points == null) {
                        return@setOnClickListener
                    }

                    buttonNext.isEnabled = false
                    buttonNextProgress.visibility = View.VISIBLE

                    saveCharacter(
                        info,
                        statsData,
                        Points(
                            fate = points.fate,
                            fortune = points.fate,
                            wounds = points.maxWounds,
                            maxWounds = points.maxWounds,
                            experience = 0,
                            resilience = points.resilience,
                            resolve = points.resilience,
                            corruption = 0,
                            sin = 0,
                            hardyWoundsBonus = 0
                        )
                    )
                }
            }

            if (currentFragmentIndex < fragmentFactories.size - 1) {
                showStep(currentFragmentIndex + 1)
            }
        }

        buttonPrevious.setOnClickListener {
            if (currentFragmentIndex != 0) {
                showStep(currentFragmentIndex - 1)
            }
        }
    }

    override fun getPartyId(): UUID = args.partyId

    private fun currentStep(): Fragment? {
        return childFragmentManager.findFragmentByTag("f$currentFragmentIndex")
    }

    private fun saveCharacter(
        info: CharacterInfoFormFragment.Data,
        statsData: CharacterStatsFormFragment.CharacteristicsData,
        points: Points
    ) {
        launch {
            val characterId = viewModel.createCharacter(args.userId, info, statsData, points)

            withContext(Dispatchers.Main) {
                toast("Your character has been created")

                val navController = findNavController()
                findNavController()
                    .navigate(
                        CharacterCreationFragmentDirections.openCharacter(characterId),
                        navOptions {
                            popUpTo(
                                try {
                                    navController.getBackStackEntry(R.id.nav_game_master)
                                    R.id.nav_game_master
                                } catch (e: IllegalArgumentException) {
                                    R.id.nav_party_list
                                }
                            ) { inclusive = false }
                        }
                    )
            }
        }

    }

    private fun showStep(index: Int) {
        wizardPager.setCurrentItem(index, false)

        buttonNext.setText(if (index == (fragmentFactories.size - 1)) R.string.button_finish else labels[index + 1])
        stepTitle.setText(labels[index])
        currentFragmentIndex = index

        if (index == 0) {
            buttonPrevious.visibility = View.GONE
        } else {
            buttonPrevious.visibility = View.VISIBLE
            buttonPrevious.setText(labels[index - 1])
        }
    }
}