package cz.muni.fi.rpg.ui.character

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.right
import cz.muni.fi.rpg.ui.PartyScopedActivity
import cz.muni.fi.rpg.ui.characterCreation.CharacterCreationActivity
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.CharacterViewModelProvider
import kotlinx.android.synthetic.main.activity_character.*
import javax.inject.Inject

class CharacterActivity : PartyScopedActivity(R.layout.activity_character) {
    @Inject
    lateinit var viewModelProvider: CharacterViewModelProvider

    private val viewModel: CharacterViewModel by viewModels {
        viewModelProvider.factory(getPartyId(), getUserId())
    }

    private val party by lazy { parties.getLive(getPartyId()).right() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.character.observe(this) {
            it.mapLeft { openCharacterCreation() }
            it.map { character -> supportActionBar?.title = character.name }
        }

        party.observe(this) { party -> supportActionBar?.subtitle = party.name }

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