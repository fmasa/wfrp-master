package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.ui.interactions.clickableWithoutIndication

@Composable
fun CheckboxWithText(
    text: String,
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    badge: @Composable () -> Unit = {},
) {
    TriStateCheckboxWithText(
        text = text,
        modifier = modifier,
        state = ToggleableState(checked),
        onClick = { onCheckedChange(!checked) },
        badge = badge,
    )
}

@Composable
fun TriStateCheckboxWithText(
    text: String,
    modifier: Modifier = Modifier,
    state: ToggleableState,
    onClick: () -> Unit,
    badge: @Composable () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.clickableWithoutIndication { onClick() },
    ) {
        TriStateCheckbox(
            state = state,
            onClick = onClick,
        )
        Text(
            text,
            modifier = Modifier.padding(start = 4.dp),
            style = MaterialTheme.typography.body2,
        )

        badge()
    }
}

@Composable
fun checkboxValue(default: Boolean): MutableState<Boolean> = rememberSaveable { mutableStateOf(default) }
