package cz.frantisekmasa.wfrp_master.common.compendium.career

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxList
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.FormDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rule
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SocialStatusInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun CareerLevelDialog(
    title: String,
    existingLevel: Career.Level?,
    existingLevelNames: Set<String>,
    onSave: suspend (Career.Level) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val data = CareerLevelDialogData.fromCareerLevel(existingLevel, existingLevelNames)

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        FormDialog(
            title = title,
            onDismissRequest = onDismissRequest,
            formData = data,
            onSave = onSave,
        ) { validate ->
            TextInput(
                label = stringResource(Str.careers_label_name),
                value = data.name,
                validate = validate,
            )

            SocialStatusInput(
                value = data.status.value,
                onValueChange = { data.status.value = it },
            )

            InputLabel(stringResource(Str.careers_label_characteristics))

            CheckboxList(
                Characteristic.values(),
                { it.localizedName },
                selected = data.characteristics,
            )

            TextInput(
                label = stringResource(Str.careers_label_income_skills),
                value = data.incomeSkills,
                validate = validate,
                multiLine = true,
                helperText = stringResource(Str.careers_comma_separated_skills_helper),
            )

            TextInput(
                label = stringResource(Str.careers_label_skills),
                value = data.skills,
                validate = validate,
                multiLine = true,
                helperText = stringResource(Str.careers_comma_separated_skills_helper),
            )

            TextInput(
                label = stringResource(Str.careers_label_talents),
                value = data.talents,
                validate = validate,
                multiLine = true,
                helperText = stringResource(Str.careers_comma_separated_talents_helper),
            )

            TextInput(
                label = stringResource(Str.careers_label_trappings),
                value = data.trappings,
                validate = validate,
                multiLine = true,
                helperText = stringResource(Str.careers_comma_separated_trappings_helper),
            )
        }
    }
}

private fun InputValue.commaSeparatedValues(): List<String> {
    val value = value

    return value
        .splitToSequence(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .toList()
}

@Stable
data class CareerLevelDialogData(
    val id: Uuid?,
    val name: InputValue,
    val characteristics: MutableState<Set<Characteristic>>,
    val status: MutableState<SocialStatus>,
    val incomeSkills: InputValue,
    val skills: InputValue,
    val talents: InputValue,
    val trappings: InputValue,
) : HydratedFormData<Career.Level> {
    override fun isValid(): Boolean {
        return listOf(name).all { it.isValid() }
    }

    override fun toValue(): Career.Level {
        val incomeSkills =
            incomeSkills
                .commaSeparatedValues()
                .map {
                    Career.Skill(expression = it, isIncomeSkill = true)
                }

        val nonIncomeSkills =
            skills
                .commaSeparatedValues()
                .map {
                    Career.Skill(expression = it, isIncomeSkill = false)
                }
                // User may fill this skill in both fields, this will deduplicate these skills
                .filter { skill -> incomeSkills.none { it.expression == skill.expression } }

        return Career.Level(
            id = id ?: uuid4(),
            name = name.value.trim(),
            status = status.value,
            skills = incomeSkills + nonIncomeSkills,
            talents = talents.commaSeparatedValues(),
            trappings = trappings.commaSeparatedValues(),
            characteristics = characteristics.value,
        )
    }

    companion object {
        @Composable
        fun fromCareerLevel(
            level: Career.Level?,
            existingLevelNames: Set<String>,
        ): CareerLevelDialogData {
            val errorLevelWithNameExists = stringResource(Str.careers_messages_level_with_name_exists)
            return CareerLevelDialogData(
                id = level?.id,
                name =
                    inputValue(
                        level?.name ?: "",
                        Rules.NotBlank(),
                        Rule {
                            if (it.trim() in existingLevelNames) {
                                errorLevelWithNameExists
                            } else {
                                null
                            }
                        },
                    ),
                status =
                    rememberSaveable(level) {
                        mutableStateOf(
                            level?.status ?: SocialStatus(SocialStatus.Tier.BRASS, 0),
                        )
                    },
                characteristics =
                    rememberSaveable(level) {
                        mutableStateOf(level?.characteristics ?: emptySet())
                    },
                incomeSkills =
                    inputValue(
                        level?.skills
                            ?.filter { it.isIncomeSkill }
                            ?.joinToString(", ") { it.expression } ?: "",
                    ),
                skills =
                    inputValue(
                        level?.skills
                            ?.filterNot { it.isIncomeSkill }
                            ?.joinToString(", ") { it.expression } ?: "",
                    ),
                talents =
                    inputValue(
                        level?.talents?.joinToString(", ") ?: "",
                    ),
                trappings =
                    inputValue(
                        level?.trappings?.joinToString(", ") ?: "",
                    ),
            )
        }
    }
}
