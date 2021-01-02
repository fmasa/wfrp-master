package cz.muni.fi.rpg.ui.characterCreation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.forms.FormData
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput

object PointsPoolForm {

    @Stable
    class Data(
        val maxWounds: MutableState<String>,
        val fatePoints: MutableState<String>,
        val resiliencePoints: MutableState<String>,
    ) : FormData {
        companion object {
            @Composable
            fun empty() = Data(
                maxWounds = savedInstanceState { "" },
                fatePoints = savedInstanceState { "" },
                resiliencePoints = savedInstanceState { "" },
            )
        }
        override fun isValid(): Boolean =
            toValue(maxWounds.value) > 0 &&
                    toValue(fatePoints.value) <= 100 &&
                    toValue(resiliencePoints.value) <= 100

        fun toPoints(): Points = Points(
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
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        TextInput(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_max_wounds),
            value = data.maxWounds.value,
            onValueChange = { data.maxWounds.value = it },
            validate = validate,
            keyboardType = KeyboardType.Number,
            maxLength = 3,
            rules = Rules(
                Rules.NotBlank(),
                { v: String -> v.toInt() > 0 } to stringResource(R.string.error_value_is_0),
                { v: String -> v.toInt() <= 100 } to stringResource(R.string.error_value_over_100),
            ),
        )

        TextInput(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_fate_points),
            value = data.fatePoints.value,
            onValueChange = { data.fatePoints.value = it },
            validate = validate,
            placeholder = "0",
            maxLength = 3,
            keyboardType = KeyboardType.Number,
            rules = Rules(
                { v: String -> toValue(v) <= 100 } to stringResource(R.string.error_value_over_100)
            )
        )

        TextInput(
            modifier = Modifier.weight(1f),
            label = stringResource(R.string.label_resilience),
            value = data.resiliencePoints.value,
            onValueChange = { data.resiliencePoints.value = it },
            validate = validate,
            placeholder = "0",
            maxLength = 3,
            keyboardType = KeyboardType.Number,
            rules = Rules(
                { v: String -> toValue(v) <= 100 } to stringResource(R.string.error_value_over_100)
            )
        )
    }
}

private fun toValue(value: String) = value.toIntOrNull() ?: 0