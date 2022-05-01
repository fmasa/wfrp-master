package cz.frantisekmasa.wfrp_master.common.characterCreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
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
                maxWounds = inputValue("", Rules.PositiveInteger()),
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
            wounds = toValue(maxWounds.value),
            maxWounds = toValue(maxWounds.value),
            resilience = toValue(resiliencePoints.value),
            resolve = toValue(resiliencePoints.value),
            sin = 0,
            experience = 0,
            spentExperience = 0,
            hardyWoundsBonus = 0,
        )
    }
}

@Composable
fun PointsPoolForm(data: PointsPoolForm.Data, validate: Boolean) {
    val labels = LocalStrings.current.points
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PointInput(data.maxWounds, labels.maxWounds, validate, null)
        PointInput(data.fatePoints, labels.fate, validate, "0")
        PointInput(data.resiliencePoints, labels.resilience, validate, "0")
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
        keyboardType = KeyboardType.Number,
    )
}

private fun toValue(value: String) = value.toIntOrNull() ?: 0
