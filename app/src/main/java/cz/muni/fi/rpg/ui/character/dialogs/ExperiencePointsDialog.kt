package cz.muni.fi.rpg.ui.character.dialogs

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.savedinstancestate.savedInstanceState
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
        var currentPointsValue by savedInstanceState { value.experience.toString() }
        var spentPointsValue by savedInstanceState { value.spentExperience.toString() }

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
                                if (currentPointsValue.isBlank() || spentPointsValue.isBlank()) {
                                    validate = true
                                    return@SaveAction
                                }

                                saving = true
                                val newPoints = value.copy(
                                    experience = currentPointsValue.toInt(),
                                    spentExperience = spentPointsValue.toInt(),
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
            ScrollableColumn(
                Modifier.padding(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small)
            ) {
                TextInput(
                    label = stringResource(R.string.label_xp_spent),
                    value = spentPointsValue,
                    onValueChange = { spentPointsValue = it },
                    validate = validate,
                    keyboardType = KeyboardType.Number,
                    rules = Rules(Rules.NotBlank())
                )

                TextInput(
                    label = stringResource(R.string.label_xp_current),
                    value = currentPointsValue,
                    onValueChange = { currentPointsValue = it },
                    validate = validate,
                    keyboardType = KeyboardType.Number,
                    rules = Rules(Rules.NotBlank())
                )
            }
        }
    }
}
