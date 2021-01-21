package cz.muni.fi.rpg.ui.characterCreation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.core.ui.forms.*

object CharacterBasicInfoForm {
    @Stable
    class Data(
        val name: InputValue,
        val socialClass: InputValue,
        val career: InputValue,
        val race: MutableState<Race>,
        val psychology: InputValue,
        val motivation: InputValue,
        val note: InputValue,
    ) : FormData {
        companion object {
            @Composable
            fun empty() = fromDefaults(null)

            @Composable
            fun fromCharacter(character: Character) = fromDefaults(character)

            @Composable
            private fun fromDefaults(character: Character?) = Data(
                name = inputValue(character?.getName() ?: "", Rules.NotBlank()),
                socialClass = inputValue(character?.getSocialClass() ?: "", Rules.NotBlank()),
                career = inputValue(character?.getCareer() ?: "", Rules.NotBlank()),
                race = savedInstanceState { character?.getRace() ?: Race.HUMAN },
                psychology = inputValue(character?.getPsychology() ?: ""),
                motivation = inputValue(character?.getMotivation() ?: ""),
                note = inputValue(character?.getNote() ?: ""),
            )
        }

        override fun isValid(): Boolean =
            name.value.isNotBlank() &&
                    socialClass.value.isNotBlank() &&
                    career.value.isNotBlank()
    }
}

@Composable
fun CharacterBasicInfoForm(
    data: CharacterBasicInfoForm.Data,
    validate: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextInput(
            label = stringResource(R.string.label_name),
            value = data.name,
            maxLength = Character.NAME_MAX_LENGTH,
            validate = validate,
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.label_social_class),
                value = data.socialClass,
                maxLength = Character.SOCIAL_CLASS_MAX_LENGTH,
                validate = validate,
            )

            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.label_career),
                value = data.career,
                maxLength = Character.CAREER_MAX_LENGTH,
                validate = validate,
            )
        }

        ChipList(
            label = stringResource(R.string.label_race),
            modifier = Modifier.padding(top = 8.dp),
            items = Race.values().map { it to stringResource(it.getReadableNameId()) },
            value = data.race.value,
            onValueChange = { data.race.value = it },
        )

        TextInput(
            label = stringResource(R.string.label_psychology_input),
            value = data.psychology,
            maxLength = Character.PSYCHOLOGY_MAX_LENGTH,
            validate = validate,
        )

        TextInput(
            label = stringResource(R.string.label_motivation_input),
            value = data.motivation,
            maxLength = Character.MOTIVATION_MAX_LENGTH,
            validate = validate,
        )

        TextInput(
            label = stringResource(R.string.label_character_note_input),
            value = data.note,
            multiLine = true,
            validate = validate,
        )
    }
}