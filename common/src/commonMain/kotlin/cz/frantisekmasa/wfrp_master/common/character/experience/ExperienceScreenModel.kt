package cz.frantisekmasa.wfrp_master.common.character.experience

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.ScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ExperienceScreenModel(
    characterId: CharacterId,
    characterRepository: CharacterRepository,
) : ScreenModel {

     val experience: Flow<Experience> = characterRepository.getLive(characterId)
        .right()
        .map {
            Experience(
                current = it.points.experience,
                spent = it.points.spentExperience,
                total = it.points.totalExperience,
            )
        }.distinctUntilChanged()

    @Immutable
    data class Experience(
        val current: Int,
        val spent: Int,
        val total: Int,
    )
}