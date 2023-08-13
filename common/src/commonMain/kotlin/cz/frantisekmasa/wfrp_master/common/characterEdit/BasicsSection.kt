package cz.frantisekmasa.wfrp_master.common.characterEdit

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.characterCreation.raceOptions
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun BasicsSection(character: Character, screenModel: CharacterScreenModel) {
    val data = BasicFormData.fromCharacter(character)

    FormScreen(
        title = stringResource(Str.character_title_basics),
        formData = data,
        onSave = {
            screenModel.update { character ->
                character.updateBasics(
                    name = it.name,
                    race = it.race,
                    motivation = it.motivation,
                    note = it.note,
                    publicName = it.publicName,
                )
            }
        }
    ) { validate ->
        TextInput(
            label = stringResource(Str.character_label_name),
            value = data.name,
            maxLength = Character.NAME_MAX_LENGTH,
            validate = validate,
        )

        if (character.type == CharacterType.NPC) {
            TextInput(
                label = stringResource(Str.character_label_public_name),
                value = data.publicName,
                maxLength = Character.NAME_MAX_LENGTH,
                validate = validate,
            )
        }

        ChipList(
            label = stringResource(Str.character_label_race),
            modifier = Modifier.padding(top = 8.dp),
            items = raceOptions(),
            value = data.race.value,
            onValueChange = { data.race.value = it },
        )

        TextInput(
            label = stringResource(Str.character_label_motivation),
            value = data.motivation,
            maxLength = Character.MOTIVATION_MAX_LENGTH,
            validate = validate,
            multiLine = true,
        )

        TextInput(
            label = stringResource(Str.character_label_note),
            value = data.note,
            maxLength = Character.NOTE_MAX_LENGTH,
            validate = validate,
            multiLine = true,
        )
    }
}

@Stable
private data class BasicFormData(
    val name: InputValue,
    val publicName: InputValue,
    val race: MutableState<Race?>,
    val motivation: InputValue,
    val note: InputValue,
) : HydratedFormData<BasicData> {
    override fun isValid(): Boolean {
        return listOf(name, motivation).all { it.isValid() }
    }

    override fun toValue() = BasicData(
        name = name.value,
        publicName = publicName.value.takeIf { it.isNotBlank() },
        race = race.value,
        motivation = motivation.value,
        note = note.value,
    )

    companion object {
        @Composable
        fun fromCharacter(character: Character) = BasicFormData(
            name = inputValue(character.name, Rules.NotBlank()),
            publicName = inputValue(character.publicName ?: "", Rules.NotBlank()),
            race = rememberSaveable(character.id) { mutableStateOf(character.race) },
            motivation = inputValue(character.motivation),
            note = inputValue(character.note),
        )
    }
}

@Immutable
private data class BasicData(
    val name: String,
    val publicName: String?,
    val race: Race?,
    val motivation: String,
    val note: String,
)
