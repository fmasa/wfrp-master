package cz.frantisekmasa.wfrp_master.common.combat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.party.combat.Advantage
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker

@Composable
fun AdvantagePicker(
    label: String,
    value: Advantage,
    onChange: (Advantage) -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberPicker(
        label = label,
        value = value.value,
        onIncrement = { onChange(value.inc()) },
        onDecrement = { onChange(value.dec()) },
        modifier = modifier,
    )
}
