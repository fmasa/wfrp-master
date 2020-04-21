package cz.muni.fi.rpg.ui.character

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.ui.PartyScopedActivity
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationActivity
import kotlinx.android.synthetic.main.activity_character.*
import kotlinx.coroutines.*
import javax.inject.Inject

class CharacterActivity : PartyScopedActivity(R.layout.activity_character),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    @Inject
    lateinit var characters: CharacterRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val party = async { parties.get(getPartyId()) }
        val character = async { characters.get(getPartyId(), getUserId()) }

        launch {
            try {
                withContext(Dispatchers.Main) {
                    supportActionBar?.title = character.await().name
                    supportActionBar?.subtitle = party.await().name
                }
            } catch (e: CharacterNotFound) {
                openCharacterCreation(e)
            }
        }

        val navController = findNavController(R.id.nav_host_fragment)
        navigation.setupWithNavController(navController)
    }

    private suspend fun openCharacterCreation(e: CharacterNotFound) {
        Log.e(localClassName, e.message ?: "Character not found", e);

        withContext(Dispatchers.Main) {
            Toast.makeText(
                applicationContext,
                "Error: Your character was not found",
                Toast.LENGTH_SHORT
            ).show()
        }

        val intent = Intent(this, CharacterCreationActivity::class.java)
        intent.putExtra(EXTRA_PARTY_ID, getPartyId().toString())

        startActivity(intent)
        finish()
    }
}