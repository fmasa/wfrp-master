package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.character.diseases.Countdown
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputLabel
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.SelectBox
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun CountdownInput(
    label: String,
    data: CountdownInputData,
    validate: Boolean,
) {
    InputLabel(label)
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.small)) {
        TextInput(
            modifier = Modifier.weight(2f),
            value = data.value,
            validate = validate,
        )

        SelectBox(
            modifier = Modifier.weight(1f),
            items = Countdown.Unit.entries,
            value = data.unit.value,
            onValueChange = { data.unit.value = it },
        )
    }
}

data class CountdownInputData(
    val value: InputValue,
    val unit: MutableState<Countdown.Unit>,
) {
    fun toValue(): Countdown {
        return Countdown(
            value = value.toInt(),
            unit = unit.value,
        )
    }

    fun isValid(): Boolean = value.isValid()
}
