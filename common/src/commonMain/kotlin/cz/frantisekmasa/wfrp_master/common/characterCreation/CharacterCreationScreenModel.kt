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
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporter
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectedCareer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class CharacterCreationScreenModel(
    private val partyId: PartyId,
    private val characters: CharacterRepository,
    private val careerCompendium: Compendium<Career>,
    private val userProvider: UserProvider,
    private val parties: PartyRepository,
) : ScreenModel {

    suspend fun getCareers(): List<Career> {
        val careers = careerCompendium.liveForParty(partyId).first()
        val party = parties.get(partyId)

        if (userProvider.userId == party.gameMasterId) {
            return careers
        }

        return careers.filter { it.isVisibleToPlayers }
    }

    suspend fun createCharacter(
        userId: UserId?,
        type: CharacterType,
        info: CharacterBasicInfoForm.Data,
        characteristicsData: CharacterCharacteristicsForm.Data,
        points: PointsPoolForm.Data,
    ): CharacterId {
        val characterId = CharacterId(partyId, uuid4().toString())

        return withContext(Dispatchers.IO) {
            try {
                Napier.d("Creating character")

                val characteristics = characteristicsData.toValue()
                val career = info.career.value

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
                    ).refreshWounds()
                )

                Reporter.recordEvent(
                    "create_character",
                    mapOf(
                        "party_id" to characterId.partyId.toString(),
                        "character_id" to characterId.id
                    )
                )

                characterId
            } catch (e: Throwable) {
                Napier.e(e.toString(), e)
                throw e
            }
        }
    }
}
