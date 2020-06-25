package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.CharacterFeatureRepository
import cz.muni.fi.rpg.model.domain.character.CharacterId
import cz.muni.fi.rpg.model.right

class ArmorViewModel(
    private val characterId: CharacterId,
    private val repository: CharacterFeatureRepository<Armor>
) : ViewModel() {
    val armor: LiveData<Armor> = repository.getLive(characterId).right()
}