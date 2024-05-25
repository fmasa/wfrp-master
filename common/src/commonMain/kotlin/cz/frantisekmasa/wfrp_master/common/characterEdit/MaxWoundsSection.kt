package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun MaxWoundsSection(
    character: Character,
    screenModel: CharacterScreenModel,
) {
    val formData = WoundsFormData.fromCharacter(character)
    FormScreen(
        title = stringResource(Str.points_wounds),
        formData = formData,
        onSave = { data ->
            screenModel.update { it.updateMaxWounds(data.maxWounds) }
        },
    ) { validate ->
        Column(Modifier.padding(top = 20.dp)) {
            TextInput(
                label = stringResource(Str.points_max_wounds),
                modifier =
                    Modifier
                        .fillMaxWidth(0.3f)
                        .padding(bottom = 12.dp),
                value = formData.maxWounds,
                maxLength = 3,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                validate = validate,
                placeholder = stringResource(Str.points_auto_max_wounds_placeholder),
            )
        }
    }
}

private data class WoundsData(
    val maxWounds: Int?,
)

private data class WoundsFormData(
    val maxWounds: InputValue,
) : HydratedFormData<WoundsData> {
    override fun isValid(): Boolean = maxWounds.isValid()

    override fun toValue(): WoundsData =
        WoundsData(
            maxWounds.value.toIntOrNull(),
        )

    companion object {
        @Composable
        fun fromCharacter(character: Character) =
            WoundsFormData(
                maxWounds =
                    inputValue(
                        character.points.maxWounds?.toString() ?: "",
                        Rules.ifNotBlank(Rules.PositiveInteger()),
                    ),
            )
    }
}
