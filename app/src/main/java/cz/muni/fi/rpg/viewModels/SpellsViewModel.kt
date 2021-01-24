package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import cz.frantisekmasa.wfrp_master.compendium.domain.Compendium
import cz.frantisekmasa.wfrp_master.compendium.domain.Spell as CompendiumSpell
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.model.domain.spells.SpellRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

class SpellsViewModel(
    private val characterId: CharacterId,
    private val spellRepository: SpellRepository,
    private val compendium: Compendium<CompendiumSpell>
) : ViewModel() {
    private val spellsFlow = spellRepository.findAllForCharacter(characterId)
    val spells: LiveData<List<Spell>> = spellsFlow.asLiveData()
    val compendiumSpellsCount: LiveData<Int> by lazy { compendiumSpells.map { it.size }.asLiveData() }

    val notUsedSpellsFromCompendium: LiveData<List<CompendiumSpell>> by lazy {
        compendiumSpells.zip(spellsFlow) { compendiumSpells, characterSpells ->
            val spellsUsedByCharacter = characterSpells.mapNotNull { it.compendiumId }.toSet()
            compendiumSpells.filter { !spellsUsedByCharacter.contains(it.id) }
        }.asLiveData()
    }

    private val compendiumSpells by lazy { compendium.liveForParty(characterId.partyId) }

    suspend fun saveSpell(spell: Spell) {
        spellRepository.save(characterId, spell)
    }

    fun removeSpell(spell: Spell) = viewModelScope.launch(Dispatchers.IO) {
        spellRepository.remove(characterId, spell.id)
    }
}