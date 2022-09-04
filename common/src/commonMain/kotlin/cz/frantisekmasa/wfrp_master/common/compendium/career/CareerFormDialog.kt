package cz.frantisekmasa.wfrp_master.common.compendium.career

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CareerFormDialog(
    title: String,
    existingCareer: CareerData?,
    onSave: suspend (CareerData) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val data = CareerFormData.fromCareerData(existingCareer)

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        FormDialog(
            title = title,
            onDismissRequest = onDismissRequest,
            formData = data,
            onSave = onSave,
        ) { validate ->
            val strings = LocalStrings.current.careers

            TextInput(
                label = strings.labelName,
                value = data.name,
                validate = validate,
                maxLength = Career.NAME_MAX_LENGTH,
            )

            TextInput(
                label = strings.labelDescription,
                value = data.description,
                multiLine = true,
                validate = validate,
                maxLength = Career.DESCRIPTION_MAX_LENGTH,
            )

            SelectBox(
                label = strings.labelSocialClass,
                items = SocialClass.values(),
                value = data.socialClass.value,
                onValueChange = { data.socialClass.value = it },
            )

            InputLabel(strings.labelRaces)

            CheckboxList(
                Race.values(),
                { it.localizedName },
                selected = data.races,
            )
        }
    }
}

@Stable
data class CareerFormData(
    val name: InputValue,
    val description: InputValue,
    val socialClass: MutableState<SocialClass>,
    val races: MutableState<Set<Race>>,
) : HydratedFormData<CareerData> {
    override fun isValid(): Boolean {
        return listOf(name, description).all { it.isValid() } && races.value.isNotEmpty()
    }

    override fun toValue() = CareerData(
        name = name.value,
        description = description.value,
        socialClass = socialClass.value,
        races = races.value,
    )

    companion object {
        @Composable
        fun fromCareerData(careerData: CareerData?) = CareerFormData(
            name = inputValue(careerData?.name ?: "", Rules.NotBlank()),
            description = inputValue(careerData?.description ?: ""),
            socialClass = rememberSaveable(careerData) {
                mutableStateOf(careerData?.socialClass ?: SocialClass.ACADEMICS)
            },
            races = rememberSaveable(careerData) {
                mutableStateOf(
                    careerData?.races ?: emptySet()
                )
            },
        )
    }
}

@Immutable
data class CareerData(
    val name: String,
    val description: String,
    val races: Set<Race>,
    val socialClass: SocialClass,
)
