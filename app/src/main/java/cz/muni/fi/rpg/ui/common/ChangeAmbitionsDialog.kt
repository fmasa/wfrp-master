package cz.muni.fi.rpg.ui.common

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import cz.frantisekmasa.wfrp_master.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.SaveAction
import cz.muni.fi.rpg.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChangeAmbitionsDialog(
    title: String,
    defaults: Ambitions,
    save: suspend (Ambitions) -> Unit,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        var shortTerm = inputValue(defaults.shortTerm)
        var longTerm = inputValue(defaults.longTerm)

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { CloseButton(onClick = onDismissRequest) },
                    title = { Text(title) },
                    actions = {
                        val coroutineScope = rememberCoroutineScope()
                        var saving by remember { mutableStateOf(false) }

                        SaveAction(
                            enabled = !saving,
                            onClick = {
                                saving = true

                                coroutineScope.launch(Dispatchers.IO) {
                                    save(
                                        Ambitions(
                                            shortTerm = shortTerm.value,
                                            longTerm = longTerm.value
                                        )
                                    )

                                    withContext(Dispatchers.Main) { onDismissRequest() }
                                }
                            }
                        )
                    }
                )
            },
        ) {
            ScrollableColumn(
                contentPadding = PaddingValues(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                TextInput(
                    label = stringResource(R.string.label_ambition_short_term),
                    value = shortTerm,
                    validate = false,
                    maxLength = Ambitions.MAX_LENGTH,
                    multiLine = true,
                )

                TextInput(
                    label = stringResource(R.string.label_ambition_long_term),
                    value = longTerm,
                    validate = false,
                    maxLength = Ambitions.MAX_LENGTH,
                    multiLine = true,
                )
            }
        }
    }
}