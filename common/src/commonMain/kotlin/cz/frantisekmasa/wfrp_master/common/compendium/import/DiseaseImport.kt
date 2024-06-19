package cz.frantisekmasa.wfrp_master.common.compendium.import

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease
import kotlinx.serialization.Serializable

@Serializable
data class DiseaseImport(
    val name: String,
    val isVisibleToPlayers: Boolean,
    val description: String,
    val contraction: String,
    val incubation: String,
    val duration: String,
    val symptoms: List<String>,
    val permanentEffects: String,
) {
    fun toDisease() =
        Disease(
            id = uuid4(),
            name = name,
            isVisibleToPlayers = isVisibleToPlayers,
            description = description,
            contraction = contraction,
            incubation = incubation,
            duration = duration,
            symptoms = symptoms,
            permanentEffects = permanentEffects,
        )

    companion object {
        fun fromDisease(disease: Disease) =
            DiseaseImport(
                name = disease.name,
                isVisibleToPlayers = disease.isVisibleToPlayers,
                description = disease.description,
                contraction = disease.contraction,
                incubation = disease.incubation,
                duration = disease.duration,
                symptoms = disease.symptoms,
                permanentEffects = disease.permanentEffects,
            )
    }
}
