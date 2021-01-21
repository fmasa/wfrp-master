package cz.frantisekmasa.wfrp_master.compendium.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.compendium.domain.Compendium
import cz.frantisekmasa.wfrp_master.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.core.domain.party.Party
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.core.utils.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CompendiumViewModel(
    private val partyId: PartyId,
    private val skillCompendium: Compendium<Skill>,
    private val talentsCompendium: Compendium<Talent>,
    private val spellCompendium: Compendium<Spell>,
    parties: PartyRepository,
) : ViewModel() {
    val party: Flow<Party> = parties.getLive(partyId).right()

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

    val skills: StateFlow<List<Skill>?> =
        skillCompendium.liveForParty(partyId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 1000L), null)

    val talents: StateFlow<List<Talent>?> =
        talentsCompendium.liveForParty(partyId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 1000L), null)

    val spells: StateFlow<List<Spell>?> =
        spellCompendium.liveForParty(partyId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 1000L), null)

}