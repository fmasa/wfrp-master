package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.Character
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.ui.AuthenticatedActivity
import cz.muni.fi.rpg.ui.PartyScopedActivity
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterEditActivity : PartyScopedActivity(R.layout.activity_character_edit),
    CoroutineScope by CoroutineScope(Dispatchers.Default), CharacterStatsCreationFragment.CharacterStatsCreationListener,
    CharacterInfoCreationFragment.CharacterInfoCreationListener {

    @Inject
    lateinit var characters: CharacterRepository
    private lateinit var currentFragment: Fragment
    private val statsCreationFragment = CharacterStatsCreationFragment().let { it.setCharacterStatsCreationListener(this) }
    private val infoCreationFragment = CharacterInfoCreationFragment().let { it.setCharacterInfoCreationListener(this) }

    override fun onStart() {
        super.onStart()

        launch {
            val character = characters.get(getPartyId(), getUserId())
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout_character_edit, infoCreationFragment)
                commit()
            }

            statsCreationFragment.setCharacterData(character)
            infoCreationFragment.setCharacterData(character)
            currentFragment = infoCreationFragment
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

        }
        finish()
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
                replace(R.id.frame_layout_character_edit, infoCreationFragment)
                commit()
            }
            currentFragment = infoCreationFragment
        } else if (id == 1) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout_character_edit, statsCreationFragment)
                commit()
            }
            currentFragment = statsCreationFragment
        }
    }
}
