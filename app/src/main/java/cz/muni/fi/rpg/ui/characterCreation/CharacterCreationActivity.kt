package cz.muni.fi.rpg.ui.characterCreation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.*
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class CharacterCreationActivity : PartyScopedActivity(R.layout.activity_character_creation),
    CoroutineScope by CoroutineScope(Dispatchers.Default), CharacterStatsCreationFragment.CharacterStatsCreationListener,
    CharacterInfoCreationFragment.CharacterInfoCreationListener {
    companion object {
        fun start(partyId: UUID, packageContext: Context) {
            val intent = Intent(packageContext, CharacterCreationActivity::class.java)
            intent.putExtra(EXTRA_PARTY_ID, partyId.toString())

            packageContext.startActivity(intent)
        }
    }
    @Inject
    lateinit var characters: CharacterRepository
    private lateinit var currentFragment: Fragment
    private val statsCreationFragment = CharacterStatsCreationFragment().let { it.setCharacterStatsCreationListener(this) }
    private val infoCreationFragment = CharacterInfoCreationFragment().let { it.setCharacterInfoCreationListener(this) }

    override fun onStart() {
        super.onStart()

        launch {
            if (characters.hasCharacterInParty(getUserId(), getPartyId())) {
                toast("You already have active character in this party")
                return@launch
            }

            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout_character_creation, infoCreationFragment)
                commit()
            }
            currentFragment = infoCreationFragment
        }
    }

    override fun nextFragment() {
        if(currentFragment == infoCreationFragment) {
            switchFragment(1)
        }
    }

    override fun previousFragment() {
        if(currentFragment == statsCreationFragment) {
            switchFragment(0)
        }
    }

    private fun switchFragment(id: Number) {
        if (id == 0) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout_character_creation, infoCreationFragment)
                commit()
            }
            currentFragment = infoCreationFragment
        }

        if (id == 1) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout_character_creation, statsCreationFragment)
                commit()
            }
            currentFragment = statsCreationFragment
        }
    }

    override fun saveCharacter() {
        launch {
            val statsAndPoints = statsCreationFragment.getData()
            val info = infoCreationFragment.getData()

            characters.save(
                getPartyId(),
                Character(
                    info.name,
                    getUserId(),
                    info.career,
                    info.race,
                    statsAndPoints.first,
                    statsAndPoints.second
                )
            )

            toast("Your character has been created")
        }
    finish()
    }

    private suspend fun toast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
        }
    }
}