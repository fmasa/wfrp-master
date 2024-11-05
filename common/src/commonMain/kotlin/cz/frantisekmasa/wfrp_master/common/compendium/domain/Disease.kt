package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Disease(
    override val id: UuidAsString,
    override val name: String,
    override val isVisibleToPlayers: Boolean = true,
    val description: String,
    val contraction: String,
    val incubation: String,
    val duration: String,
    val symptoms: List<String>,
    val permanentEffects: String,
) : CompendiumItem<Disease>() {
    init {
        require(name.isNotEmpty())
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        contraction.requireMaxLength(CONTRACTION_MAX_LENGTH, "contraction")
        incubation.requireMaxLength(INCUBATION_MAX_LENGTH, "incubation")
        duration.requireMaxLength(DURATION_MAX_LENGTH, "duration")
        symptoms.forEach { it.requireMaxLength(SYMPTOMS_MAX_LENGTH, "symptom") }
        description.requireMaxLength(DESCRIPTION_MAX_LENGTH, "description")
        permanentEffects.requireMaxLength(PERMANENT_EFFECTS_MAX_LENGTH, "permanentEffects")
    }

    override fun duplicate() =
        copy(
            id = uuid4(),
            name = duplicateName(name, NAME_MAX_LENGTH),
        )

    override fun replace(original: Disease) = copy(id = original.id)

    override fun changeVisibility(isVisibleToPlayers: Boolean) = copy(isVisibleToPlayers = isVisibleToPlayers)

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val CONTRACTION_MAX_LENGTH = 500
        const val INCUBATION_MAX_LENGTH = 100
        const val DURATION_MAX_LENGTH = 500
        const val SYMPTOMS_MAX_LENGTH = 200
        const val DESCRIPTION_MAX_LENGTH = 1000
        const val PERMANENT_EFFECTS_MAX_LENGTH = 200
    }
}
