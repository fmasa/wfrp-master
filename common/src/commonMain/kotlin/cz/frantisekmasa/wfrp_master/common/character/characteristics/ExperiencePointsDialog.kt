package cz.frantisekmasa.wfrp_master.common.character.characteristics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ExperiencePointsDialog(
    value: Points,
    save: suspend (Points) -> Unit,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val currentPoints = inputValue(value.experience.toString(), Rules.NonNegativeInteger())
        val spentPoints = inputValue(value.spentExperience.toString(), Rules.NonNegativeInteger())

        val coroutineScope = rememberCoroutineScope()
        var validate by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(stringResource(Str.points_experience)) },
                    actions = {
                        var saving by remember { mutableStateOf(false) }
                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                if (!currentPoints.isValid() || !spentPoints.isValid()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true
                                val newPoints = value.copy(
                                    experience = currentPoints.toInt(),
                                    spentExperience = spentPoints.toInt(),
                                )

                                coroutineScope.launch(Dispatchers.IO) {
                                    save(newPoints)

                                    onDismissRequest()
                                }
                            }
                        )
                    }
                )
            }
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                PointInput(
                    spentPoints,
                    stringResource(Str.points_label_spent_experience),
                    validate,
                )
                PointInput(
                    currentPoints,
                    stringResource(Str.points_label_current_experience),
                    validate,
                )
            }
        }
    }
}

@Composable
private fun PointInput(value: InputValue, label: String, validate: Boolean) {
    TextInput(
        label = label,
        value = value,
        validate = validate,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    )
}
