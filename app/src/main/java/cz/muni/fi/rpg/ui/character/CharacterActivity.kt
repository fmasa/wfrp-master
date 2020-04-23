package cz.muni.fi.rpg.ui.character

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.ui.PartyScopedActivity
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationActivity
import kotlinx.android.synthetic.main.activity_character.*
import javax.inject.Inject

class CharacterActivity : PartyScopedActivity(R.layout.activity_character) {
    @Inject
    lateinit var characters: CharacterRepository

    private val party by lazy { parties.getLive(getPartyId()) }
    private val character by lazy { characters.getLive(getPartyId(), getUserId()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        character.observe(this) {
            it.mapLeft { openCharacterCreation() }
            it.map { character -> supportActionBar?.title = character.name }
        }

        party.observe(this) {
            it.map { party ->
                supportActionBar?.subtitle = party.name
            }
        }

        val navController = findNavController(R.id.nav_host_fragment)
        navigation.setupWithNavController(navController)
    }

    private fun openCharacterCreation() {
        Log.e(localClassName, "Character not found");

        Toast.makeText(
            applicationContext,
            "Error: Your character was not found",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this, CharacterCreationActivity::class.java)
        intent.putExtra(EXTRA_PARTY_ID, getPartyId().toString())

        startActivity(intent)
        finish()
    }
}