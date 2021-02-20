package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import cz.frantisekmasa.wfrp_master.compendium.domain.*
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.utils.right

class CompendiumViewModel(
    private val partyId: PartyId,
    private val skillCompendium: Compendium<Skill>,
    private val talentsCompendium: Compendium<Talent>,
    private val spellCompendium: Compendium<Spell>,
    private val blessingCompendium: Compendium<Blessing>,
    private val miracleCompendium: Compendium<Miracle>,
    parties: PartyRepository,
) : ViewModel() {
    val party: LiveData<Party> = parties.getLive(partyId).right().asLiveData()

    val skills: LiveData<List<Skill>> = skillCompendium.liveForParty(partyId).asLiveData()
    val talents: LiveData<List<Talent>> = talentsCompendium.liveForParty(partyId).asLiveData()
    val spells: LiveData<List<Spell>> = spellCompendium.liveForParty(partyId).asLiveData()
    val blessings: LiveData<List<Blessing>> = blessingCompendium.liveForParty(partyId).asLiveData()
    val miracles: LiveData<List<Miracle>> = miracleCompendium.liveForParty(partyId).asLiveData()

    suspend fun save(skill: Skill) {
        skillCompendium.saveItems(partyId, skill)
    }

    suspend fun saveMultipleSkills(skills: List<Skill>) {
        skillCompendium.saveItems(partyId, *skills.toTypedArray())
    }

    suspend fun remove(skill: Skill) {
        skillCompendium.remove(partyId, skill)
    }

    suspend fun save(talent: Talent) {
        talentsCompendium.saveItems(partyId, talent)
    }

    suspend fun remove(talent: Talent) {
        talentsCompendium.remove(partyId, talent)
    }

    suspend fun saveMultipleTalents(talents: List<Talent>) {
        talentsCompendium.saveItems(partyId, *talents.toTypedArray())
    }

    suspend fun save(spell: Spell) {
        spellCompendium.saveItems(partyId, spell)
    }

    suspend fun remove(spell: Spell) {
        spellCompendium.remove(partyId, spell)
    }

    suspend fun saveMultipleSpells(spells: List<Spell>) {
        spellCompendium.saveItems(partyId, *spells.toTypedArray())
    }
}