package cz.frantisekmasa.wfrp_master.compendium.domain

import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.CompendiumItem
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Parcelize
@Serializable
data class Talent(
    @Contextual override val id: UUID,
    override val name: String,
    val maxTimesTaken: String,
    val description: String,
) : CompendiumItem<Talent>() {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val MAX_TIMES_TAKEN_MAX_LENGTH = 100
        const val DESCRIPTION_MAX_LENGTH = 1500
    }

    init {
        require(name.isNotEmpty())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
        require(maxTimesTaken.length <= MAX_TIMES_TAKEN_MAX_LENGTH) { "Maximum length of is $MAX_TIMES_TAKEN_MAX_LENGTH" }
    }

    override fun duplicate() = copy(id = UUID.randomUUID(), name = duplicateName())
}
