package cz.muni.fi.rpg.ui.characterCreation

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.core.ui.forms.FormData
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput

object CharacterBasicInfoForm {
    @Stable
    class Data(
        val name: MutableState<String>,
        val socialClass: MutableState<String>,
        val career: MutableState<String>,
        val race: MutableState<Race>,
        val psychology: MutableState<String>,
        val motivation: MutableState<String>,
        val note: MutableState<String>,
    ) : FormData {
        companion object {
            @Composable
            fun empty() = Data(
                name = savedInstanceState { "" },
                socialClass = savedInstanceState { "" },
                career = savedInstanceState { "" },
                race = savedInstanceState { Race.HUMAN },
                psychology = savedInstanceState { "" },
                motivation = savedInstanceState { "" },
                note = savedInstanceState { "" },
            )

            @Composable
            fun fromCharacter(character: Character) = Data(
                name = savedInstanceState { character.getName() },
                socialClass = savedInstanceState { character.getSocialClass() },
                career = savedInstanceState { character.getCareer() },
                race = savedInstanceState { character.getRace() },
                psychology = savedInstanceState { character.getPsychology() },
                motivation = savedInstanceState { character.getMotivation() },
                note = savedInstanceState { character.getNote() },
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
            value = data.name.value,
            onValueChange = { data.name.value = it },
            maxLength = Character.NAME_MAX_LENGTH,
            validate = validate,
            rules = Rules(Rules.NotBlank()),
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.label_social_class),
                value = data.socialClass.value,
                onValueChange = { data.socialClass.value = it },
                maxLength = Character.SOCIAL_CLASS_MAX_LENGTH,
                validate = validate,
                rules = Rules(Rules.NotBlank()),
            )

            TextInput(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.label_career),
                value = data.career.value,
                onValueChange = { data.career.value = it },
                maxLength = Character.CAREER_MAX_LENGTH,
                rules = Rules(Rules.NotBlank()),
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
            value = data.psychology.value,
            onValueChange = { data.psychology.value = it },
            maxLength = Character.PSYCHOLOGY_MAX_LENGTH,
            validate = validate,
        )

        TextInput(
            label = stringResource(R.string.label_motivation_input),
            value = data.motivation.value,
            onValueChange = { data.motivation.value = it },
            maxLength = Character.MOTIVATION_MAX_LENGTH,
            validate = validate,
        )

        TextInput(
            label = stringResource(R.string.label_character_note_input),
            value = data.note.value,
            onValueChange = { data.note.value = it },
            maxLength = Character.NOTE_MAX_LENGTH,
            multiLine = true,
            validate = validate,
        )
    }
}