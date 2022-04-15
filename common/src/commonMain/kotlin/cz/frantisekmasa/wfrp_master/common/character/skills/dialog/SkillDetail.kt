package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun SkillDetail(
    skill: Skill,
    onDismissRequest: () -> Unit,
    onAdvancesChange: (advances: Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(skill.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            AdvancesBar(
                advances = skill.advances,
                minAdvances = if (skill.advanced) 1 else 0,
                onAdvancesChange = onAdvancesChange,
            )

            val strings = LocalStrings.current

            Column(Modifier.padding(Spacing.bodyPadding)) {
                SingleLineTextValue(
                    label = strings.skills.labelCharacteristic,
                    skill.characteristic.localizedName,
                )

                SingleLineTextValue(
                    label = strings.skills.labelAdvanced,
                    value = strings.commonUi.boolean(skill.advanced),
                )

                Text(
                    text = skill.description,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun AdvancesBar(
    advances: Int,
    minAdvances: Int,
    onAdvancesChange: (advances: Int) -> Unit
) {
    SubheadBar {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(LocalStrings.current.skills.labelAdvances)
            NumberPicker(
                value = advances,
                onIncrement = { onAdvancesChange(advances + 1) },
                onDecrement = {
                    if (advances > minAdvances) {
                        onAdvancesChange(advances - 1)
                    }
                }
            )
        }
    }
}
