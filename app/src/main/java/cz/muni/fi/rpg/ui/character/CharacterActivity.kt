package cz.muni.fi.rpg.ui.character

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.os.bundleOf
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.character.observe(this) {
            it.mapLeft { openCharacterCreation() }
            it.map { character -> supportActionBar?.title = character.name }
        }

        partyViewModel.party
            .right()
            .observe(this) { supportActionBar?.subtitle = it.name }

        initializeNavigation()
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

    private fun initializeNavigation() {
        val arguments = bundleOf(
            CharacterStatsFragment.ARG_PARTY_ID to getPartyId().toString(),
            CharacterStatsFragment.ARG_USER_ID to getUserId()
        )

        val navController = findNavController(R.id.nav_host_fragment)
        navController.setGraph(R.navigation.mobile_navigation, arguments)
        navigation.setupWithNavController(navController)
        navigation.setOnNavigationItemSelectedListener { item ->
            navController.popBackStack(item.itemId, true)
            navController.navigate(item.itemId, arguments)

            return@setOnNavigationItemSelectedListener false
        }
    }
}