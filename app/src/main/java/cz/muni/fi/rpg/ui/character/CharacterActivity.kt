package cz.muni.fi.rpg.ui.character

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterNotFound
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.model.domain.party.Party
import cz.muni.fi.rpg.ui.PartyScopedActivity
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationActivity
import kotlinx.coroutines.*
import javax.inject.Inject

class CharacterActivity : PartyScopedActivity(R.layout.activity_character),
    CoroutineScope by CoroutineScope(Dispatchers.Default) {
    @Inject
    lateinit var characters: CharacterRepository

    private lateinit var party: Party
    private lateinit var character: Character

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launch {
            try {
                party = parties.get(getPartyId())
                character = characters.get(getPartyId(), getUserId())

                withContext(Dispatchers.Main) {
                    supportActionBar?.title = character.name
                    supportActionBar?.subtitle = party.name
                }
            } catch (e: CharacterNotFound) {
                openCharacterCreation(e)
            }
        }
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