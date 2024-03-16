package cz.frantisekmasa.wfrp_master.common.compendium.domain

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Career(
    @Contextual override val id: Uuid,
    override val name: String,
    val description: String,
    val socialClass: SocialClass,
    val races: Set<Race>,
    val levels: List<Level>,
    override val isVisibleToPlayers: Boolean = true,
) : CompendiumItem<Career>() {

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 3000
    }

    init {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(description.length <= DESCRIPTION_MAX_LENGTH)
        require(races.isNotEmpty())
        require(levels.map { it.name }.toSet().size == levels.size) {
            "Duplicate name for Career level"
        }
    }

    override fun replace(original: Career): Career {
        val originalLevelsByName = original.levels.associateBy { it.name }

        return copy(
            id = original.id,
            levels = levels.map {
                val originalLevel = originalLevelsByName[it.name]

                if (originalLevel != null) it.copy(id = originalLevel.id) else it
            }
        )
    }

    override fun duplicate(): Career = copy(id = uuid4(), name = duplicateName())

    override fun changeVisibility(isVisibleToPlayers: Boolean) =
        copy(isVisibleToPlayers = isVisibleToPlayers)

    @Parcelize
    @Serializable
    data class Level(
        @Contextual val id: Uuid,
        val name: String,
        val status: SocialStatus,
        val characteristics: Set<Characteristic>,
        val skills: List<Skill>,
        val talents: List<String>,
        val trappings: List<String>,
    ) : Parcelable {
        companion object {
            const val NAME_MAX_LENGTH = 50
        }

        init {
            require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        }
    }

    @Parcelize
    @Serializable
    data class Skill(
        val expression: String,
        val isIncomeSkill: Boolean,
    ) : Parcelable {
        init {
            require(expression.isNotBlank())
        }
    }
}
