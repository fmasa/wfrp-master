package cz.muni.fi.rpg.ui.character.skills.dialog

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.primitives.NumberPicker
import cz.frantisekmasa.wfrp_master.core.ui.primitives.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SubheadBar
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.skills.Skill

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

            Column(Modifier.padding(Spacing.bodyPadding)) {
                SingleLineTextValue(
                    R.string.label_skill_characteristic,
                    stringResource(skill.characteristic.getNameId()),
                )

                SingleLineTextValue(
                    labelRes = R.string.label_skill_advanced,
                    value = stringResource(
                        if (skill.advanced) R.string.boolean_yes else R.string.boolean_no
                    )
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
            Text(stringResource(R.string.label_advances))
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
