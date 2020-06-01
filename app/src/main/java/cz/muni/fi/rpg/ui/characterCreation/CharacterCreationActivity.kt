package cz.muni.fi.rpg.ui.characterCreation

import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.*
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.android.synthetic.main.activity_character_creation.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterCreationActivity : PartyScopedActivity(R.layout.activity_character_creation),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    companion object {
        const val EXTRA_CHARACTER_ID = "characterId"
    }

    @Inject
    lateinit var characters: CharacterRepository
    private lateinit var currentFragment: Fragment
    private val statsCreationFragment = CharacterStatsFormFragment()
    private val infoCreationFragment = CharacterInfoFormFragment()

    private var characterInfo: CharacterInfoFormFragment.CharacterInfo? = null

    private val characterId by lazy {
        intent.getStringExtra(EXTRA_CHARACTER_ID)
            ?: throw IllegalAccessException("'${EXTRA_CHARACTER_ID}' must be provided")
    }

    override fun onStart() {
        super.onStart()

        launch {
            if (characters.hasCharacterInParty(getUserId(), getPartyId())) {
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
                getPartyId(),
                Character(info.name, characterId, info.career, info.race, stats, points)
            )
            toast("Your character has been created")

            finish()
        }

    }

    private fun showStep(
        @StringRes titleResId: Int,
        fragment: Fragment,
        @StringRes previousButtonTextResId: Int?,
        @StringRes nextButtonTextResId: Int
    ) {
        supportFragmentManager.beginTransaction().apply {
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
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}