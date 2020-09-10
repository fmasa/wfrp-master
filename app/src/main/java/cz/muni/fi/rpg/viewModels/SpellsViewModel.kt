package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.domain.spells.Spell
import cz.muni.fi.rpg.model.domain.spells.SpellRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpellsViewModel(
    private val characterId: CharacterId,
    private val spellRepository: SpellRepository
) : ViewModel(), CoroutineScope by CoroutineScope(Dispatchers.IO) {

    val spells: LiveData<List<Spell>> = spellRepository.findAllForCharacter(characterId)

    suspend fun saveSpell(spell: Spell) {
        spellRepository.save(characterId, spell)
    }

    fun removeSpell(spell: Spell) = launch {
        spellRepository.remove(characterId, spell.id)
    }
}