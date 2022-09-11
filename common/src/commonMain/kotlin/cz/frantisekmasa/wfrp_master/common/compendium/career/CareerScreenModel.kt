package cz.frantisekmasa.wfrp_master.common.compendium.career

import arrow.core.Either
import cafe.adriel.voyager.core.model.ScreenModel
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.exceptions.CompendiumItemNotFound
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import kotlinx.coroutines.flow.Flow

class CareerScreenModel(
    private val careerCompendium: Compendium<Career>,
) : ScreenModel {

    fun getCareer(partyId: PartyId, careerId: Uuid): Flow<Either<CompendiumItemNotFound, Career>> {
        return careerCompendium.getLive(partyId, careerId)
    }

    suspend fun update(partyId: PartyId, career: Career) {
        careerCompendium.saveItems(partyId, career)
    }

    suspend fun saveLevel(partyId: PartyId, careerId: Uuid, level: Career.Level) {
        val career = careerCompendium.getItem(partyId, careerId)

        val existingIndex = career.levels.indexOfFirst { it.id == level.id }

        if (existingIndex == -1) {
            careerCompendium.saveItems(
                partyId,
                career.copy(levels = career.levels + level)
            )
        } else {
            val levels = career.levels.toMutableList()
            levels[existingIndex] = level

            careerCompendium.saveItems(
                partyId,
                career.copy(levels = levels)
            )
        }
    }
}