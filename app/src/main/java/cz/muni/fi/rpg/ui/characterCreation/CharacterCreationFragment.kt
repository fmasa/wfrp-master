package cz.muni.fi.rpg.ui.characterCreation

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.*
import cz.muni.fi.rpg.ui.common.BaseFragment
import cz.muni.fi.rpg.viewModels.AuthenticationViewModel
import kotlinx.android.synthetic.main.fragment_character_creation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.viewmodel.ext.android.sharedViewModel

class CharacterCreationFragment(
    private val characters: CharacterRepository
) : BaseFragment(R.layout.fragment_character_creation),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    private val args: CharacterCreationFragmentArgs by navArgs()
    private val authentication: AuthenticationViewModel by sharedViewModel()

    private val labels = arrayOf(
        R.string.title_character_creation_info,
        R.string.title_character_stats,
        R.string.title_character_creation_points
    )

    private val fragments = arrayOf<Fragment>(
        CharacterInfoFormFragment(),
        CharacterStatsFormFragment(),
        CharacterPointsFormFragment()
    )

    private var currentFragmentIndex = 0

    private var characterInfo: CharacterInfoFormFragment.CharacterInfo? = null
    private var characterStatsData: Stats? = null

    override fun onStart() {
        super.onStart()

        launch {
            if (characters.hasCharacterInParty(authentication.getUserId(), args.partyId)) {
                toast("You already have active character in this party")
                return@launch
            }

            withContext(Dispatchers.Main) { showStep(0) }
        }

        buttonNext.setOnClickListener {
            when (val currentFragment = fragments[currentFragmentIndex]) {
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
                    val stats = characterStatsData
                    val points = currentFragment.submit()

                    if (info == null || stats == null || points == null) {
                        return@setOnClickListener
                    }

                    saveCharacter(
                        info,
                        stats,
                        Points(
                            fate = points.fate,
                            fortune = points.fate,
                            wounds = points.maxWounds,
                            maxWounds = points.maxWounds,
                            experience = 0,
                            resilience = points.resilience,
                            resolve = points.resilience,
                            corruption = 0,
                            sin = 0
                        )
                    )
                }
            }

            if (currentFragmentIndex < fragments.size - 1) {
                showStep(currentFragmentIndex + 1)
            }
        }

        buttonPrevious.setOnClickListener {
            if (currentFragmentIndex != 0) {
                showStep(currentFragmentIndex - 1)
            }
        }
    }

    private fun saveCharacter(
        info: CharacterInfoFormFragment.CharacterInfo,
        stats: Stats,
        points: Points
    ) {
        launch {
            characters.save(
                args.partyId,
                Character(
                    name = info.name,
                    userId = authentication.getUserId(),
                    career = info.career,
                    socialClass = info.socialClass,
                    race = info.race,
                    stats = stats,
                    points = points
                )
            )
            toast("Your character has been created")

            findNavController().popBackStack()
        }

    }

    private fun showStep(index: Int) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_character_creation, fragments[index])
            commit()
        }

        buttonNext.setText(if (index == (fragments.size - 1)) R.string.button_finish else labels[index + 1])
        stepTitle.setText(labels[index])
        currentFragmentIndex = index

        if (index == 0) {
            buttonPrevious.visibility = View.GONE
        } else {
            buttonPrevious.visibility = View.VISIBLE
            buttonPrevious.setText(labels[index - 1])
        }
    }

    private suspend fun toast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}