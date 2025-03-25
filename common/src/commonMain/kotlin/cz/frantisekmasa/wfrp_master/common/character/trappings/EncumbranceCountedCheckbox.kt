package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.CheckboxWithText
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.InfoIcon
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun EncumbranceCountedCheckbox(state: MutableState<Boolean>) {
    Row {
        CheckboxWithText(
            modifier = Modifier.weight(1f),
            text = stringResource(Str.trappings_label_encumbrance_counted),
            checked = state.value,
            onCheckedChange = { state.value = it },
        )
        InfoIcon(
            title = stringResource(Str.trappings_label_encumbrance_counted),
            text = stringResource(Str.trappings_label_encumbrance_counted_subtext),
        )
    }
}
