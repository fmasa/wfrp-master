package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTip
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.UserTipCard
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
            screenModel.update { it.updateMaxWounds(data.maxWounds, data.hardyTalent) }
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

            if (character.hasHardyTalent) {
                if (formData.hardyTalent.value) {
                    UserTipCard(UserTip.HARDY_TALENTS)
                }

                CheckboxWithText(
                    text = stringResource(Str.points_label_hardy),
                    checked = formData.hardyTalent.value,
                    onCheckedChange = { formData.hardyTalent.value = it },
                )
            }
        }
    }
}

private data class WoundsData(
    val maxWounds: Int?,
    val hardyTalent: Boolean,
)

private data class WoundsFormData(
    val maxWounds: InputValue,
    val hardyTalent: MutableState<Boolean>,
) : HydratedFormData<WoundsData> {
    override fun isValid(): Boolean = maxWounds.isValid()

    override fun toValue(): WoundsData =
        WoundsData(
            maxWounds.value.toIntOrNull(),
            hardyTalent.value,
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
                hardyTalent = rememberSaveable { mutableStateOf(character.hasHardyTalent) },
            )
    }
}
