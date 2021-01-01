package cz.muni.fi.rpg.viewModels

import androidx.lifecycle.ViewModel
import cz.muni.fi.rpg.model.domain.armour.Armor
import cz.muni.fi.rpg.model.domain.character.CharacterFeatureRepository
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.core.utils.right
import kotlinx.coroutines.flow.Flow

class ArmorViewModel(
    characterId: CharacterId,
    repository: CharacterFeatureRepository<Armor>
) : ViewModel() {
    val armor: Flow<Armor> = repository.getLive(characterId).right()
}