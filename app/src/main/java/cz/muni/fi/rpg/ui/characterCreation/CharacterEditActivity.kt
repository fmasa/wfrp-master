package cz.muni.fi.rpg.ui.characterCreation

import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.CharacterRepository
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class CharacterEditActivity : PartyScopedActivity(R.layout.activity_character_edit),
    CoroutineScope by CoroutineScope(Dispatchers.Default), CharacterStatsCreationFragment.CharacterStatsCreationListener,
    CharacterInfoCreationFragment.CharacterInfoCreationListener {

    companion object {
        const val EXTRA_CHARACTER_ID = "characterId"
    }

    @Inject
    lateinit var characters: CharacterRepository
    private lateinit var currentFragment: Fragment
    private val statsCreationFragment = CharacterStatsCreationFragment().let { it.setCharacterStatsCreationListener(this) }
    private val infoCreationFragment = CharacterInfoCreationFragment().let { it.setCharacterInfoCreationListener(this) }

    private val characterId by lazy {
        intent.getStringExtra(EXTRA_CHARACTER_ID)
            ?: throw IllegalAccessException("'${EXTRA_CHARACTER_ID}' must be provided")
    }

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
            val character = characters.get(getPartyId(), characterId)

            character.update(info.name, info.career, info.race, statsAndPoints.first, statsAndPoints.second)
            characters.save(getPartyId(), character)

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
