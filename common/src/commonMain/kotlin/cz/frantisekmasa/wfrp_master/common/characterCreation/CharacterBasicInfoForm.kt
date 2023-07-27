package cz.frantisekmasa.wfrp_master.common.characterCreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.ChipList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectedCareer
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
        val career: MutableState<SelectedCareer?>,
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
                career = rememberSaveable {
                    mutableStateOf(
                        character?.let {
                            val compendiumCareer = character.compendiumCareer
                            compendiumCareer?.let {
                                SelectedCareer.CompendiumCareer(
                                    compendiumCareer,
                                    character.status,
                                )
                            } ?: SelectedCareer.NonCompendiumCareer(
                                character.career,
                                character.socialClass,
                            )
                        }
                    )
                },
                race = rememberSaveable {
                    mutableStateOf(if (character == null) Race.HUMAN else character.race)
                },
                psychology = inputValue(character?.psychology ?: ""),
                motivation = inputValue(character?.motivation ?: ""),
                note = inputValue(character?.note ?: ""),
                status = rememberSaveable {
                    mutableStateOf(
                        character?.status
                            ?: SocialStatus(SocialStatus.Tier.BRASS, 0)
                    )
                },
                careers = careers,
            )
        }

        override fun isValid(): Boolean = name.value.isNotBlank()
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

        CareerSelectBox(
            careers = data.careers,
            value = data.career.value,
            onValueChange = {
                if (it is SelectedCareer.CompendiumCareer && data.career.value != it) {
                    data.status.value = it.socialStatus
                }
                data.career.value = it
            },
        )

        SocialStatusInput(
            value = data.status.value,
            onValueChange = { data.status.value = it },
        )

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
            maxLength = Character.NOTE_MAX_LENGTH,
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
