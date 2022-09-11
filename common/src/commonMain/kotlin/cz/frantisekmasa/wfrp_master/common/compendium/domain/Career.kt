package cz.frantisekmasa.wfrp_master.common.compendium.domain

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
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
) : CompendiumItem<Career>() {

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 3000
    }

    init {
        require(name.isNotBlank() && name.length <= NAME_MAX_LENGTH)
        require(description.length <= DESCRIPTION_MAX_LENGTH)
        require(races.isNotEmpty())
    }

    override fun duplicate(): Career = copy(name = duplicateName())

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
    ) : Parcelable
}
