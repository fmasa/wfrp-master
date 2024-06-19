package cz.frantisekmasa.wfrp_master.common.character.diseases

import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Disease
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.DiseaseRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository

class CharacterDiseaseDetailScreenModel(
    characterId: CharacterId,
    private val diseaseRepository: DiseaseRepository,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Disease>(
        characterId,
        diseaseRepository,
        userProvider,
        partyRepository,
    ) {
    suspend fun saveDisease(disease: Disease) = diseaseRepository.save(characterId, disease)
}
