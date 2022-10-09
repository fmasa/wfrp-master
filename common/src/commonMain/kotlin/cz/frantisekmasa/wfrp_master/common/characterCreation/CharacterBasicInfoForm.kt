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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Character
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterType
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Race
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CareerSelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SocialStatusInput
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
        val customCareer: MutableState<Boolean>,
        val compendiumCareer: MutableState<Character.CompendiumCareer?>,
        val race: MutableState<Race?>,
        val psychology: InputValue,
        val motivation: InputValue,
        val note: InputValue,
        val status: MutableState<SocialStatus>,
        val careers: List<Career>,
    ) : FormData {
        companion object {
            @Composable
            fun empty(characterType: CharacterType, careers: List<Career>): Data {
                return fromDefaults(null, characterType, careers)
            }

            @Composable
            private fun fromDefaults(character: Character?, characterType: CharacterType, careers: List<Career>) = Data(
                characterType = characterType,
                name = inputValue(character?.name ?: "", Rules.NotBlank()),
                publicName = inputValue(character?.publicName ?: ""),
                socialClass = inputValue(character?.socialClass ?: ""),
                career = inputValue(character?.career ?: ""),
                race = rememberSaveable {
                    mutableStateOf(if (character == null) Race.HUMAN else character.race)
                },
                psychology = inputValue(character?.psychology ?: ""),
                motivation = inputValue(character?.motivation ?: ""),
                note = inputValue(character?.note ?: ""),
                customCareer = rememberSaveable {
                    mutableStateOf(character != null && character.career != "")
                },
                compendiumCareer = rememberSaveable {
                    mutableStateOf(character?.compendiumCareer)
                },
                status = rememberSaveable {
                    mutableStateOf(
                        character?.status
                            ?: SocialStatus(SocialStatus.Tier.BRASS, 0)
                    )
                },
                careers = careers,
            )
        }

        override fun isValid(): Boolean =
            name.value.isNotBlank() && (customCareer.value || compendiumCareer.value != null)
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

        CheckboxWithText(
            strings.labelCustomCareer,
            checked = data.customCareer.value,
            onCheckedChange = { data.customCareer.value = !data.customCareer.value },
        )

        if (data.customCareer.value) {
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

            SocialStatusInput(
                value = data.status.value,
                onValueChange = { data.status.value = it },
            )
        } else {
            CareerSelectBox(
                careers = data.careers,
                value = data.compendiumCareer.value,
                onValueChange = { data.compendiumCareer.value = it },
                validate = validate,
            )
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
