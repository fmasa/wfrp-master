package cz.frantisekmasa.wfrp_master.common.compendium.import

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class CareerImport(
    val name: String,
    val description: String,
    val socialClass: SocialClass,
    val races: Set<Race>,
    val levels: List<Level>,
    val isVisibleToPlayers: Boolean = true,
) {
    init {
        require(name.isNotBlank()) { "Career name cannot be blank" }
        name.requireMaxLength(Career.NAME_MAX_LENGTH, "career name")
        description.requireMaxLength(Career.DESCRIPTION_MAX_LENGTH, "career description")
        require(races.isNotEmpty()) { "At least one race required for career \"$name\"" }
        require(levels.map { it.name }.toSet().size == levels.size) {
            "Duplicate name for career level of career \"$name\""
        }
    }

    fun toCareer() = Career(
        id = uuid4(),
        name = name,
        description = description,
        socialClass = socialClass,
        races = races,
        levels = levels.map {
            Career.Level(
                id = uuid4(),
                name = it.name,
                status = it.status,
                characteristics = it.characteristics,
                skills = it.skills.map { skill ->
                    Career.Skill(
                        skill.expression,
                        skill.isIncomeSkill
                    )
                },
                talents = it.talents,
                trappings = it.trappings,
            )
        },
        isVisibleToPlayers = isVisibleToPlayers,
    )

    @Serializable
    @Immutable
    data class Level(
        val name: String,
        val status: SocialStatus,
        val characteristics: Set<Characteristic>,
        val skills: List<Skill>,
        val talents: List<String>,
        val trappings: List<String>,
    )

    @Serializable
    @Immutable
    data class Skill(
        val expression: String,
        val isIncomeSkill: Boolean,
    ) {
        init {
            require(expression.isNotBlank()) { "Career skill expression cannot be blank" }
        }
    }

    companion object {
        fun fromCareer(career: Career) = CareerImport(
            name = career.name,
            description = career.description,
            socialClass = career.socialClass,
            races = career.races,
            levels = career.levels.map {
                Level(
                    name = it.name,
                    status = it.status,
                    characteristics = it.characteristics,
                    skills = it.skills.map { skill ->
                        Skill(
                            skill.expression,
                            skill.isIncomeSkill
                        )
                    },
                    talents = it.talents,
                    trappings = it.trappings,
                )
            },
            isVisibleToPlayers = career.isVisibleToPlayers,
        )
    }
}
