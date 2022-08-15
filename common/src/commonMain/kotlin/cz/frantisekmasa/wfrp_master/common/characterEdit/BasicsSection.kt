package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.characterCreation.raceOptions
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun BasicsSection(character: Character, screenModel: CharacterScreenModel) {
    val data = BasicFormData.fromCharacter(character)
    val strings = LocalStrings.current.character

    FormScreen(
        title = strings.titleBasics,
        formData = data,
        onSave = {
            screenModel.update { character ->
                character.updateBasics(
                    name = it.name,
                    race = it.race,
                    motivation = it.motivation,
                )
            }
        }
    ) { validate ->
        TextInput(
            label = strings.labelName,
            value = data.name,
            maxLength = Character.NAME_MAX_LENGTH,
            validate = validate,
        )

        ChipList(
            label = strings.labelRace,
            modifier = Modifier.padding(top = 8.dp),
            items = raceOptions(),
            value = data.race.value,
            onValueChange = { data.race.value = it },
        )

        TextInput(
            label = strings.labelMotivation,
            value = data.motivation,
            maxLength = Character.MOTIVATION_MAX_LENGTH,
            validate = validate,
        )
    }
}

private data class BasicFormData(
    val name: InputValue,
    val race: MutableState<Race?>,
    val motivation: InputValue,
) : HydratedFormData<BasicData> {
    override fun isValid(): Boolean {
        return listOf(name, motivation).all { it.isValid() }
    }

    override fun toValue() = BasicData(
        name = name.value,
        race = race.value,
        motivation = motivation.value,
    )

    companion object {
        @Composable
        fun fromCharacter(character: Character) = BasicFormData(
            name = inputValue(character.name, Rules.NotBlank()),
            race = rememberSaveable(character.id) { mutableStateOf(character.race) },
            motivation = inputValue(character.motivation),
        )
    }
}

@Immutable
private data class BasicData(
    val name: String,
    val race: Race?,
    val motivation: String,
)