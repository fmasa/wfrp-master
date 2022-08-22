package cz.frantisekmasa.wfrp_master.common.characterCreation

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
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBoxLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


object CharacterBasicInfoForm {
    @Stable
    class Data(
        val characterType: CharacterType,
        val name: InputValue,
        val publicName: InputValue,
        val socialClass: InputValue,
        val career: InputValue,
        val race: MutableState<Race?>,
        val psychology: InputValue,
        val motivation: InputValue,
        val note: InputValue,
        val socialTier: MutableState<SocialStatus.Tier>,
        val socialStanding: MutableState<Int>,
    ) : FormData {
        companion object {
            @Composable
            fun empty(characterType: CharacterType) = fromDefaults(null, characterType)

            @Composable
            private fun fromDefaults(character: Character?, characterType: CharacterType) = Data(
                characterType = characterType,
                name = inputValue(character?.name ?: "", Rules.NotBlank()),
                publicName = inputValue(character?.publicName ?: ""),
                socialClass = inputValue(character?.socialClass ?: "", Rules.NotBlank()),
                career = inputValue(character?.career ?: "", Rules.NotBlank()),
                race = rememberSaveable {
                    mutableStateOf(if (character == null) Race.HUMAN else character.race)
                },
                psychology = inputValue(character?.psychology ?: ""),
                motivation = inputValue(character?.motivation ?: ""),
                note = inputValue(character?.note ?: ""),
                socialTier = rememberSaveable {
                    mutableStateOf(character?.status?.tier ?: SocialStatus.Tier.BRASS)
                },
                socialStanding = rememberSaveable {
                    mutableStateOf(character?.status?.standing ?: 0)
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
    val strings = LocalStrings.current.character

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextInput(
            label = strings.labelName,
            value = data.name,
            maxLength = Character.NAME_MAX_LENGTH,
            validate = validate,
        )

        if (data.characterType == CharacterType.NPC) {
            TextInput(
                label = strings.labelPublicName,
                value = data.publicName,
                maxLength = Character.NAME_MAX_LENGTH,
                validate = validate,
            )
        }

        ChipList(
            label = strings.labelRace,
            modifier = Modifier.padding(top = 8.dp),
            items = raceOptions(),
            value = data.race.value,
            onValueChange = { data.race.value = it },
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextInput(
                modifier = Modifier.weight(1f),
                label = strings.labelClass,
                value = data.socialClass,
                maxLength = Character.SOCIAL_CLASS_MAX_LENGTH,
                validate = validate,
            )

            TextInput(
                modifier = Modifier.weight(1f),
                label = strings.labelCareer,
                value = data.career,
                maxLength = Character.CAREER_MAX_LENGTH,
                validate = validate,
            )
        }

        // TODO: Turn into component that can be reused in Edit UI
        Column {
            SelectBoxLabel(strings.labelStatus)
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
            label = strings.labelPsychology,
            value = data.psychology,
            maxLength = Character.PSYCHOLOGY_MAX_LENGTH,
            validate = validate,
        )

        TextInput(
            label = strings.labelMotivation,
            value = data.motivation,
            maxLength = Character.MOTIVATION_MAX_LENGTH,
            validate = validate,
        )

        TextInput(
            label = strings.labelNote,
            value = data.note,
            multiLine = true,
            validate = validate,
        )
    }
}

@Composable
@Stable
fun raceOptions(): List<Pair<Race?, String>> {
    return Race.values().map { it to it.localizedName } +
        listOf(null to LocalStrings.current.races.custom)
}
