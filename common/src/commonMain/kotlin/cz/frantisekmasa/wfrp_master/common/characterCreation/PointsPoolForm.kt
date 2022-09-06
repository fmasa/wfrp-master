package cz.frantisekmasa.wfrp_master.common.characterCreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.HydratedFormData
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

object PointsPoolForm {

    @Stable
    class Data(
        val maxWounds: InputValue,
        val fatePoints: InputValue,
        val resiliencePoints: InputValue,
    ) : HydratedFormData<Points> {
        companion object {
            @Composable
            fun empty() = Data(
                maxWounds = inputValue("", Rules.IfNotBlank(Rules.PositiveInteger())),
                fatePoints = inputValue("", Rules.IfNotBlank(Rules(Rules.NonNegativeInteger()))),
                resiliencePoints = inputValue(
                    "",
                    Rules.IfNotBlank(Rules(Rules.NonNegativeInteger()))
                ),
            )
        }

        override fun isValid(): Boolean =
            listOf(maxWounds, fatePoints, resiliencePoints).all { it.isValid() }

        override fun toValue(): Points = Points(
            corruption = 0,
            fate = toValue(fatePoints.value),
            fortune = toValue(fatePoints.value),
            wounds = 0,
            maxWounds = maxWounds.value.toIntOrNull(),
            resilience = toValue(resiliencePoints.value),
            resolve = toValue(resiliencePoints.value),
            sin = 0,
            experience = 0,
            spentExperience = 0,
        )
    }
}

@Composable
fun PointsPoolForm(data: PointsPoolForm.Data, validate: Boolean) {
    val strings = LocalStrings.current.points
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PointInput(data.maxWounds, strings.maxWounds, validate, strings.autoMaxWoundsPlaceholder)
        PointInput(data.fatePoints, strings.fate, validate, "0")
        PointInput(data.resiliencePoints, strings.resilience, validate, "0")
    }
}

@Composable
private fun RowScope.PointInput(
    value: InputValue,
    label: String,
    validate: Boolean,
    placeholder: String?,
) {
    TextInput(
        modifier = Modifier.weight(1f),
        label = label,
        value = value,
        validate = validate,
        placeholder = placeholder,
        maxLength = 2,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    )
}

private fun toValue(value: String) = value.toIntOrNull() ?: 0
