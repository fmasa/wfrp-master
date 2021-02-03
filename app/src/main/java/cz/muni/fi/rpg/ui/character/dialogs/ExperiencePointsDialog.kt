package cz.muni.fi.rpg.ui.character.dialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.Rules
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.domain.character.Points
import cz.frantisekmasa.wfrp_master.core.ui.forms.InputValue
import cz.frantisekmasa.wfrp_master.core.ui.forms.inputValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                    title = { Text(stringResource(R.string.xp_points)) },
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

                                    withContext(Dispatchers.Main) {
                                        onDismissRequest()
                                    }
                                }
                            }
                        )
                    }
                )
            }) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding (Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                PointInput(spentPoints, R.string.label_xp_spent, validate)
                PointInput(currentPoints, R.string.label_xp_current, validate)
            }
        }
    }
}

@Composable
private fun PointInput(value: InputValue, @StringRes labelRes: Int, validate: Boolean) {
    TextInput(
        label = stringResource(R.string.label_xp_current),
        value = value,
        validate = validate,
        keyboardType = KeyboardType.Number,
    )
}
