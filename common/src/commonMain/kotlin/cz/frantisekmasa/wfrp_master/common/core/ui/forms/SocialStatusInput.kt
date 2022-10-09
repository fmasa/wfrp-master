package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.character.SocialStatus
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun SocialStatusInput(value: SocialStatus, onValueChange: (SocialStatus) -> Unit) {
    Column {
        SelectBoxLabel(LocalStrings.current.character.labelStatus)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SelectBox(
                value = value.tier,
                onValueChange = { onValueChange(value.copy(tier = it)) },
                items = SocialStatus.Tier.values(),
                modifier = Modifier.fillMaxWidth(0.6f),
            )
            NumberPicker(
                value = value.standing,
                onIncrement = {
                    onValueChange(
                        value.copy(standing = (value.standing + 1))
                    )
                },
                onDecrement = {
                    onValueChange(
                        value.copy(standing = (value.standing - 1).coerceAtLeast(0))
                    )
                },
            )
        }
    }
}
