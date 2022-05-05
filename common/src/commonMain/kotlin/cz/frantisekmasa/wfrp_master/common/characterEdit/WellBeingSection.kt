package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun WellBeingSection(character: Character, screenModel: CharacterScreenModel) {
    val formData = WellBeingFormData.fromCharacter(character)

    FormScreen(
        title = LocalStrings.current.character.titleWellBeing,
        formData = formData,
        onSave = {
            screenModel.update { character ->
                character.updateWellBeing(it.corruptionPoints, it.psychology)
            }
        }
    ) { validate ->
        NumberPicker(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            label = LocalStrings.current.points.corruption,
            value = formData.corruptionPoints.value,
            onIncrement = { formData.corruptionPoints.value++ },
            onDecrement = {
                val currentValue = formData.corruptionPoints.value

                if (currentValue > 0) {
                    formData.corruptionPoints.value--
                }
            }
        )

        TextInput(
            label = LocalStrings.current.character.labelPsychology,
            value = formData.psychology,
            maxLength = Character.PSYCHOLOGY_MAX_LENGTH,
            validate = validate,
        )
    }
}


@Stable
private data class WellBeingFormData(
    val corruptionPoints: MutableState<Int>,
    val psychology: InputValue,
) : HydratedFormData<WellBeingData> {
    override fun isValid(): Boolean = psychology.isValid()

    override fun toValue(): WellBeingData = WellBeingData(
        corruptionPoints = corruptionPoints.value,
        psychology = psychology.value,
    )

    companion object {
        @Composable
        fun fromCharacter(character: Character): WellBeingFormData {
            return WellBeingFormData(
                corruptionPoints = rememberSaveable(character) {
                    mutableStateOf(character.points.corruption)
                },
                psychology = inputValue(character.psychology),
            )
        }
    }
}

@Immutable
private data class WellBeingData(
    val corruptionPoints: Int,
    val psychology: String,
)