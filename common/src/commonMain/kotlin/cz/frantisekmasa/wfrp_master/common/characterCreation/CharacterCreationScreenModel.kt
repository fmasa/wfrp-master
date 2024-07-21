package cz.frantisekmasa.wfrp_master.common.characterCreation

import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.auth.UserId
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.EncounterId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectedCareer
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.encounters.domain.EncounterRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext

class CharacterCreationScreenModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository,
    private val encounters: EncounterRepository,
    careerCompendium: Compendium<Career>,
    private val userProvider: UserProvider,
    private val firestore: FirebaseFirestore,
    parties: PartyRepository,
) : ScreenModel {
    val careers: Flow<List<Career>> =
        careerCompendium.liveForParty(partyId)
            .combine(parties.getLive(partyId).right()) { careers, party ->
                if (userProvider.userId == party.gameMasterId) {
                    careers
                } else {
                    careers.filter { it.isVisibleToPlayers }
                }
            }

    suspend fun createCharacter(
        userId: UserId?,
        type: CharacterType,
        info: CharacterBasicInfoForm.Data,
        characteristicsData: CharacterCharacteristicsForm.Data,
        points: PointsPoolForm.Data,
        encounterId: EncounterId?,
    ): CharacterId {
        val characterId = CharacterId(partyId, uuid4().toString())

        return withContext(Dispatchers.IO) {
            try {
                Napier.d("Creating character")

                val characteristics = characteristicsData.toValue()
                val career = info.career.value

                firestore.runTransaction {
                    characters.save(
                        characterId.partyId,
                        Character(
                            id = characterId.id,
                            type = type,
                            name = info.name.value,
                            publicName = info.publicName.value.takeIf { it.isNotBlank() },
                            userId = userId,
                            career = if (career is SelectedCareer.NonCompendiumCareer) career.careerName else "",
                            socialClass = if (career is SelectedCareer.NonCompendiumCareer) career.socialClass else "",
                            status = info.status.value,
                            race = info.race.value,
                            characteristicsBase = characteristics.base,
                            characteristicsAdvances = characteristics.advances,
                            points = points.toValue(),
                            psychology = info.psychology.value,
                            motivation = info.motivation.value,
                            note = info.note.value,
                            compendiumCareer = if (career is SelectedCareer.CompendiumCareer) career.value else null,
                        ).refreshWounds(),
                    )

                    val encounter = encounterId?.let { encounters.find(it) }

                    if (encounter != null) {
                        encounters.save(
                            partyId,
                            encounter.withCharacterCount(characterId.id, 1),
                        )
                    }
                }

                Reporting.record {
                    characterCreated(
                        characterId = characterId,
                        encounterId = encounterId,
                        type = type,
                    )
                }

                characterId
            } catch (e: Throwable) {
                Napier.e(e.toString(), e)
                throw e
            }
        }
    }
}
