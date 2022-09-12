package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CareerSelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SocialStatusInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Composable
fun CareerSection(character: Character, screenModel: CharacterScreenModel) {
    val data = CareerFormData.fromCharacter(character)
    val strings = LocalStrings.current.character

    FormScreen(
        title = strings.titleCareer,
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
    ) { validate ->
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

        CheckboxWithText(
            strings.labelCustomCareer,
            checked = data.customCareer.value,
            onCheckedChange = { data.customCareer.value = !data.customCareer.value },
        )

        if (data.customCareer.value) {
            TextInput(
                label = strings.labelClass,
                value = data.socialClass,
                maxLength = Character.SOCIAL_CLASS_MAX_LENGTH,
                validate = validate,
            )

            TextInput(
                label = strings.labelCareer,
                value = data.careerName,
                maxLength = Character.CAREER_MAX_LENGTH,
                validate = validate,
            )
        } else {
            CareerSelectBox(
                careers = careers,
                value = data.compendiumCareer.value,
                onValueChange = { data.compendiumCareer.value = it },
                validate = validate,
            )
        }

        SocialStatusInput(
            value = data.status.value,
            onValueChange = { data.status.value = it },
        )
    }
}

data class CareerFormData(
    val careerName: InputValue,
    val socialClass: InputValue,
    val status: MutableState<SocialStatus>,
    val compendiumCareer: MutableState<Character.CompendiumCareer?>,
    val customCareer: MutableState<Boolean>,
) : HydratedFormData<CareerData> {

    override fun isValid(): Boolean = careerName.isValid() &&
        (customCareer.value || compendiumCareer.value != null)

    override fun toValue(): CareerData = CareerData(
        careerName = if (customCareer.value)careerName.value else "",
        socialClass = if (customCareer.value) socialClass.value else "",
        status = status.value,
        compendiumCareer = if (!customCareer.value) compendiumCareer.value else null
    )

    companion object {
        @Composable
        fun fromCharacter(character: Character) = CareerFormData(
            careerName = inputValue(character.career),
            socialClass = inputValue(character.socialClass),
            status = rememberSaveable(character.id) { mutableStateOf(character.status) },
            compendiumCareer = rememberSaveable(character.id) { mutableStateOf(character.compendiumCareer) },
            customCareer = rememberSaveable(character.id) { mutableStateOf(character.compendiumCareer == null) },
        )
    }
}

data class CareerData(
    val careerName: String,
    val socialClass: String,
    val status: SocialStatus,
    val compendiumCareer: Character.CompendiumCareer?
)
