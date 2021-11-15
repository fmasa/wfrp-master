package cz.muni.fi.rpg.ui.characterCreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBoxLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.NumberPicker
import cz.muni.fi.rpg.R

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
        val socialTier: MutableState<SocialStatus.Tier>,
        val socialStanding: MutableState<Int>,
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
                race = rememberSaveable { mutableStateOf(character?.getRace() ?: Race.HUMAN) },
                psychology = inputValue(character?.getPsychology() ?: ""),
                motivation = inputValue(character?.getMotivation() ?: ""),
                note = inputValue(character?.getNote() ?: ""),
                socialTier = rememberSaveable {
                    mutableStateOf(character?.getStatus()?.tier ?: SocialStatus.Tier.BRASS)
                },
                socialStanding = rememberSaveable {
                    mutableStateOf(character?.getStatus()?.standing ?: 0)
                },
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

        ChipList(
            label = stringResource(R.string.label_race),
            modifier = Modifier.padding(top = 8.dp),
            items = Race.values().map { it to it.localizedName },
            value = data.race.value,
            onValueChange = { data.race.value = it },
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

        Column {
            SelectBoxLabel(stringResource(R.string.label_status))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SelectBox(
                    value = data.socialTier.value,
                    onValueChange = { data.socialTier.value = it },
                    items = SocialStatus.Tier.values(),
                    modifier = Modifier.fillMaxWidth(0.6f),
                )
                NumberPicker(
                    value = data.socialStanding.value,
                    onIncrement = { data.socialStanding.value++ },
                    onDecrement = { data.socialStanding.value = maxOf(0, data.socialStanding.value - 1) },
                )
            }
        }

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
