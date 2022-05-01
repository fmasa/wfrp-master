package cz.frantisekmasa.wfrp_master.common.character.talents.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


@Composable
fun TalentDetail(
    talent: Talent,
    onDismissRequest: () -> Unit,
    onTimesTakenChange: (timesTaken: Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(talent.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            TimesTakenBar(talent.taken, onTimesTakenChange)

            Column(Modifier.padding(Spacing.bodyPadding)) {
                Text(talent.description)
            }
        }
    }
}

@Composable
private fun TimesTakenBar(timesTaken: Int, onTimesTakenChange: (timesTaken: Int) -> Unit) {
    SubheadBar {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = LocalStrings.current.talents.labelTimesTaken,
                modifier = Modifier.weight(1f),
            )
            NumberPicker(
                value = timesTaken,
                onIncrement = { onTimesTakenChange(timesTaken + 1) },
                onDecrement = {
                    if (timesTaken > 1) {
                        onTimesTakenChange(timesTaken - 1)
                    }
                }
            )
        }
    }
}
