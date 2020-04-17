package cz.muni.fi.rpg.ui.characterCreation

import android.widget.Toast
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.*
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * TODO: Add fragments to let user configure his character
 * @see {https://gitlab.com/fmasa/pv239-project/-/issues/6}
 */
class CharacterCreationActivity : PartyScopedActivity(R.layout.activity_character_creation),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    @Inject
    lateinit var characters: CharacterRepository

    override fun onStart() {
        super.onStart()

        launch {
            if (characters.hasCharacterInParty(getUserId(), getPartyId())) {
                toast("You already have active character in this party")
                return@launch
            }

            characters.save(
                getPartyId(),
                Character(
                    "Unknown Soldier",
                    getUserId(),
                    "Wizard",
                    Race.ELF,
                    Stats(
                        wounds = 6,
                        weaponSkill = 35,
                        ballisticSkill = 40,
                        strength = 30,
                        toughness = 60,
                        agility = 41,
                        intelligence = 32,
                        willPower = 40,
                        fellowship = 42
                    ),
                    Points(insanity = 0, fate = 3, fortune = 3)
                )
            )

            toast("Your character have been created")
        }

        finish()
    }

    private suspend fun toast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}