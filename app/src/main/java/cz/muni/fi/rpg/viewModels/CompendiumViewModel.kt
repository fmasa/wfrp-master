package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.compendium.Compendium
import cz.muni.fi.rpg.model.domain.compendium.Skill
import cz.muni.fi.rpg.model.domain.compendium.Spell
import cz.muni.fi.rpg.model.domain.compendium.Talent
import java.util.UUID

class CompendiumViewModel(
    private val partyId: UUID,
    private val skillCompendium: Compendium<Skill>,
    private val talentsCompendium: Compendium<Talent>,
    private val spellCompendium: Compendium<Spell>,
) : ViewModel() {

    suspend fun save(skill: Skill) {
        skillCompendium.saveItem(partyId, skill)
    }

    suspend fun remove(skill: Skill) {
        skillCompendium.remove(partyId, skill)
    }

    suspend fun save(talent: Talent) {
        talentsCompendium.saveItem(partyId, talent)
    }

    suspend fun remove(talent: Talent) {
        talentsCompendium.remove(partyId, talent)
    }

    val skills: LiveData<List<Skill>> by lazy { skillCompendium.liveForParty(partyId) }
    val talents: LiveData<List<Talent>> by lazy { talentsCompendium.liveForParty(partyId) }
    val spells: LiveData<List<Spell>> by lazy { spellCompendium.liveForParty(partyId) }
}