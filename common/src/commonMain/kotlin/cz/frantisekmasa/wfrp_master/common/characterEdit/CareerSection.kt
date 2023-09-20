package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CareerSelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectedCareer
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SocialStatusInput
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Composable
fun CareerSection(character: Character, screenModel: CharacterScreenModel) {
    val data = CareerFormData.fromCharacter(character)

    FormScreen(
        title = stringResource(Str.character_title_career),
        formData = data,
        onSave = {
            screenModel.update { character ->
                character.updateCareer(
                    careerName = it.careerName,
                    socialClass = it.socialClass,
                    status = it.status,
                    compendiumCareer = it.compendiumCareer,
                )
            }
        }
    ) { _ ->
        val (careers, setCareers) = rememberSaveable {
            mutableStateOf<List<Career>?>(null)
        }

        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                setCareers(screenModel.allCareers.first())
            }
        }

        if (careers == null) {
            FullScreenProgress()
            return@FormScreen
        }

        CareerSelectBox(
            careers = careers,
            value = data.career.value,
            onValueChange = {
                if (it is SelectedCareer.CompendiumCareer && data.career.value != it) {
                    data.status.value = it.socialStatus
                }
                data.career.value = it
            },
        )

        SocialStatusInput(
            value = data.status.value,
            onValueChange = { data.status.value = it },
        )
    }
}

data class CareerFormData(
    val career: MutableState<SelectedCareer>,
    val status: MutableState<SocialStatus>,
) : HydratedFormData<CareerData> {

    override fun isValid(): Boolean = true

    override fun toValue(): CareerData {
        return when (val career = career.value) {
            is SelectedCareer.CompendiumCareer -> CareerData(
                careerName = "",
                socialClass = "",
                status = status.value,
                compendiumCareer = career.value,
            )
            is SelectedCareer.NonCompendiumCareer -> CareerData(
                careerName = career.careerName,
                socialClass = career.socialClass,
                status = status.value,
                compendiumCareer = null,
            )
        }
    }

    companion object {
        @Composable
        fun fromCharacter(character: Character) = CareerFormData(
            career = rememberSaveable(character.id) {
                mutableStateOf(
                    character.compendiumCareer?.let {
                        SelectedCareer.CompendiumCareer(it, character.status)
                    } ?: SelectedCareer.NonCompendiumCareer(
                        character.career,
                        character.socialClass,
                    )
                )
            },
            status = rememberSaveable(character.id) { mutableStateOf(character.status) },
        )
    }
}

data class CareerData(
    val careerName: String,
    val socialClass: String,
    val status: SocialStatus,
    val compendiumCareer: Character.CompendiumCareer?
)
