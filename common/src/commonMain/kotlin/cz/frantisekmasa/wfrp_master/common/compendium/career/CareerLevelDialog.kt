package cz.frantisekmasa.wfrp_master.common.compendium.career

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
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
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SocialStatusInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun CareerLevelDialog(
    title: String,
    existingLevel: Career.Level?,
    onSave: suspend (Career.Level) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val data = CareerLevelDialogData.fromCareerLevel(existingLevel)

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        FormDialog(
            title = title,
            onDismissRequest = onDismissRequest,
            formData = data,
            onSave = onSave,
        ) { validate ->
            val strings = LocalStrings.current.careers

            TextInput(
                label = strings.labelName,
                value = data.name,
                validate = validate,
            )

            SocialStatusInput(
                value = data.status.value,
                onValueChange = { data.status.value = it },
            )

            InputLabel(strings.labelCharacteristics)

            CheckboxList(
                Characteristic.values(),
                { it.localizedName },
                selected = data.characteristics,
            )

            TextInput(
                label = strings.labelIncomeSkills,
                value = data.incomeSkills,
                validate = validate,
                multiLine = true,
                helperText = strings.commaSeparatedSkillsHelper,
            )

            TextInput(
                label = strings.labelSkills,
                value = data.skills,
                validate = validate,
                multiLine = true,
                helperText = strings.commaSeparatedSkillsHelper,
            )

            TextInput(
                label = strings.labelTalents,
                value = data.talents,
                validate = validate,
                multiLine = true,
                helperText = strings.commaSeparatedTalentsHelper,
            )

            TextInput(
                label = strings.labelTrappings,
                value = data.trappings,
                validate = validate,
                multiLine = true,
                helperText = strings.commaSeparatedTrappingsHelper,
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
        val incomeSkills = incomeSkills
            .commaSeparatedValues()
            .map {
                Career.Skill(expression = it, isIncomeSkill = true)
            }

        val nonIncomeSkills = skills
            .commaSeparatedValues()
            .map {
                Career.Skill(expression = it, isIncomeSkill = false)
            }
            // User may fill this skill in both fields, this will deduplicate these skills
            .filter { skill -> incomeSkills.none { it.expression == skill.expression } }

        return Career.Level(
            id = id ?: uuid4(),
            name = name.value,
            status = status.value,
            skills = incomeSkills + nonIncomeSkills,
            talents = talents.commaSeparatedValues(),
            trappings = trappings.commaSeparatedValues(),
            characteristics = characteristics.value,
        )
    }

    companion object {
        @Composable
        fun fromCareerLevel(level: Career.Level?) = CareerLevelDialogData(
            id = level?.id,
            name = inputValue(level?.name ?: "", Rules.NotBlank()),
            status = rememberSaveable(level) {
                mutableStateOf(
                    level?.status ?: SocialStatus(SocialStatus.Tier.BRASS, 0)
                )
            },
            characteristics = rememberSaveable(level) {
                mutableStateOf(level?.characteristics ?: emptySet())
            },
            incomeSkills = inputValue(
                level?.skills
                    ?.filter { it.isIncomeSkill }
                    ?.joinToString(", ") { it.expression } ?: ""
            ),
            skills = inputValue(
                level?.skills
                    ?.filterNot { it.isIncomeSkill }
                    ?.joinToString(", ") { it.expression } ?: ""
            ),
            talents = inputValue(
                level?.talents?.joinToString(", ") ?: ""
            ),
            trappings = inputValue(
                level?.trappings?.joinToString(", ") ?: ""
            )
        )
    }
}
