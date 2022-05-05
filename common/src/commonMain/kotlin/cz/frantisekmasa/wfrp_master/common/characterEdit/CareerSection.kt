package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBoxLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

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
                )
            }
        }
    ) { validate ->

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

        Column {
            SelectBoxLabel(strings.labelStatus)
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SelectBox(
                    value = data.status.value.tier,
                    onValueChange = { data.status.value = data.status.value.copy(tier = it) },
                    items = SocialStatus.Tier.values(),
                    modifier = Modifier.fillMaxWidth(0.6f),
                )

                NumberPicker(
                    value = data.status.value.standing,
                    onIncrement = {
                        data.status.value = data.status.value.copy(
                            standing = data.status.value.standing + 1
                        )
                    },
                    onDecrement = {
                        val currentStanding = data.status.value.standing

                        if (currentStanding > 0) {
                            data.status.value = data.status.value.copy(
                                standing = data.status.value.standing - 1
                            )
                        }
                    },
                )
            }
        }
    }
}

data class CareerFormData(
    val careerName: InputValue,
    val socialClass: InputValue,
    val status: MutableState<SocialStatus>,
) : HydratedFormData<CareerData> {

    override fun isValid(): Boolean = careerName.isValid()

    override fun toValue(): CareerData = CareerData(
        careerName = careerName.value,
        socialClass = socialClass.value,
        status = status.value,
    )

    companion object {
        @Composable
        fun fromCharacter(character: Character) = CareerFormData(
            careerName = inputValue(character.career, Rules.NotBlank()),
            socialClass = inputValue(character.socialClass, Rules.NotBlank()),
            status = rememberSaveable(character.id) { mutableStateOf(character.status) },
        )
    }
}

data class CareerData(
    val careerName: String,
    val socialClass: String,
    val status: SocialStatus,
)