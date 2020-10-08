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
    val skills: LiveData<List<Skill>> by lazy { skillCompendium.liveForParty(partyId) }
    val talents: LiveData<List<Talent>> by lazy { talentsCompendium.liveForParty(partyId) }
    val spells: LiveData<List<Spell>> by lazy { spellCompendium.liveForParty(partyId) }
}