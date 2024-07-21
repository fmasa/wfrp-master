package cz.frantisekmasa.wfrp_master.common.character.diseases

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.journal.rules.DiseaseSymptomProvider
import cz.frantisekmasa.wfrp_master.common.core.CharacterItemScreenModel
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Disease
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.DiseaseRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import kotlinx.coroutines.flow.map

class CharacterDiseaseDetailScreenModel(
    characterId: CharacterId,
    private val diseaseRepository: DiseaseRepository,
    private val diseaseSymptomProvider: DiseaseSymptomProvider,
    userProvider: UserProvider,
    partyRepository: PartyRepository,
) : CharacterItemScreenModel<Disease>(
        characterId,
        diseaseRepository,
        userProvider,
        partyRepository,
    ) {
    suspend fun saveDisease(disease: Disease) {
        Reporting.record { characterItemAdded("disease") }
        diseaseRepository.save(characterId, disease)
    }

    fun getDiseaseDetail(diseaseId: Uuid) =
        diseaseSymptomProvider.addSymptoms(
            characterId.partyId,
            getItem(diseaseId),
        ) { it.orNull()?.symptoms ?: emptyList() }
            .map { (diseaseOrError, symptoms) ->
                diseaseOrError.bimap(
                    { it },
                    { disease ->
                        CharacterDiseaseDetailScreenState(
                            disease = disease,
                            symptoms = symptoms,
                        )
                    },
                )
            }
}
