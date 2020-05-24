package cz.muni.fi.rpg.ui.characterCreation

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.character.*
import cz.muni.fi.rpg.ui.PartyScopedActivity
import kotlinx.android.synthetic.main.fragment_character_stats.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CharacterCreationActivity : PartyScopedActivity(R.layout.activity_character_creation),
    CoroutineScope by CoroutineScope(Dispatchers.Default), CharacterStatsCreationFragment.CharacterStatsCreationListener,
    CharacterInfoCreationFragment.CharacterInfoCreationListener {
    @Inject
    lateinit var characters: CharacterRepository
    private val statsCreationFragment = CharacterStatsCreationFragment()
    private val infoCreationFragment = CharacterInfoCreationFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        }
    }

     override fun onAttachFragment(fragment: Fragment) {
        if (fragment is CharacterStatsCreationFragment) {
            fragment.setCharacterStatsCreationListener(this)
        }

         if (fragment is CharacterInfoCreationFragment) {
             fragment.setCharacterInfoCreationListener(this)
         }
    }



    public override fun switchFragment(id: Number) {
        if (id == 0)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_character_creation, infoCreationFragment)
            commit()
        }
        if (id == 1)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout_character_creation, statsCreationFragment)
                commit()
            }
    }

    public override fun saveCharacter() {
        launch {

            characters.save(
                getPartyId(),
                Character(
                    infoCreationFragment.characterName.text.toString(),
                    getUserId(),
                    infoCreationFragment.characterCareer.text.toString(),
                    infoCreationFragment.characterRace,
                    Stats(
                        weaponSkill = statsCreationFragment.weaponSkill.text.toString().toInt(),
                        ballisticSkill = statsCreationFragment.ballisticSkill.text.toString().toInt(),
                        strength = statsCreationFragment.strength.text.toString().toInt(),
                        toughness = statsCreationFragment.toughness.text.toString().toInt(),
                        agility = statsCreationFragment.agility.text.toString().toInt(),
                        intelligence = statsCreationFragment.intelligence.text.toString().toInt(),
                        willPower = statsCreationFragment.willPower.text.toString().toInt(),
                        fellowship = statsCreationFragment.fellowship.text.toString().toInt(),
                        magic = statsCreationFragment.magic.text.toString().toInt()
                    ),
                    Points(insanity = 0, fate = 3, fortune = 3, maxWounds = 6, wounds = 6)
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