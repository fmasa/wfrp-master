package cz.muni.fi.rpg.ui.characterCreation

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
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

    private lateinit var currentFragment: Fragment
    private val statsCreationFragment = CharacterStatsFormFragment()
    private val infoCreationFragment = CharacterInfoFormFragment()

    private var characterInfo: CharacterInfoFormFragment.CharacterInfo? = null

    override fun onStart() {
        super.onStart()

        launch {
            if (characters.hasCharacterInParty(authentication.getUserId(), args.partyId)) {
                toast("You already have active character in this party")
                return@launch
            }

            withContext(Dispatchers.Main) { showInfoFragment() }
        }

        buttonNext.setOnClickListener {
            when (currentFragment) {
                infoCreationFragment -> {
                    characterInfo = infoCreationFragment.submit() ?: return@setOnClickListener
                    showStatsFragment()
                }
                statsCreationFragment -> {
                    val characterInfo = this.characterInfo
                    val data = statsCreationFragment.submit()

                    if (characterInfo == null || data == null) {
                        return@setOnClickListener
                    }

                    saveCharacter(
                        characterInfo,
                        data.stats,
                        Points(
                            insanity = 0,
                            fate = data.fatePoints,
                            fortune = data.fatePoints,
                            wounds = data.maxWounds,
                            maxWounds = data.maxWounds
                        )
                    )
                }
            }
        }

        buttonPrevious.setOnClickListener {
            if (currentFragment == statsCreationFragment) {
                showInfoFragment()
            }
        }
    }

    private fun showInfoFragment() {
        showStep(
            R.string.title_character_creation_info,
            infoCreationFragment,
            null,
            R.string.button_edit
        )
    }

    private fun showStatsFragment() {
        showStep(
            R.string.title_character_creation_stats,
            statsCreationFragment,
            R.string.button_edit_info,
            R.string.button_finish
        )
    }

    private fun saveCharacter(
        info: CharacterInfoFormFragment.CharacterInfo,
        stats: Stats,
        points: Points
    ) {
        launch {
            characters.save(
                args.partyId,
                Character(info.name, authentication.getUserId(), info.career, info.race, stats, points)
            )
            toast("Your character has been created")

            findNavController().popBackStack()
        }

    }

    private fun showStep(
        @StringRes titleResId: Int,
        fragment: Fragment,
        @StringRes previousButtonTextResId: Int?,
        @StringRes nextButtonTextResId: Int
    ) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_character_creation, fragment)
            commit()
        }
        currentFragment = fragment
        buttonNext.setText(nextButtonTextResId)
        stepTitle.setText(titleResId)

        if (previousButtonTextResId != null) {
            buttonPrevious.visibility = View.VISIBLE
            buttonPrevious.setText(previousButtonTextResId)
        } else {
            buttonPrevious.visibility = View.GONE
        }
    }

    private suspend fun toast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}