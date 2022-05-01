package cz.frantisekmasa.wfrp_master.common.ambitions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.TextInput
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.inputValue
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ChangeAmbitionsDialog(
    title: String,
    defaults: Ambitions,
    save: suspend (Ambitions) -> Unit,
    onDismissRequest: () -> Unit,
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        val shortTerm = inputValue(defaults.shortTerm)
        val longTerm = inputValue(defaults.longTerm)

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

                                    onDismissRequest()
                                }
                            }
                        )
                    }
                )
            },
        ) {
            Column(
                Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
                verticalArrangement = Arrangement.spacedBy(Spacing.small),
            ) {
                val strings = LocalStrings.current.ambition

                TextInput(
                    label = strings.labelShortTerm,
                    value = shortTerm,
                    validate = false,
                    maxLength = Ambitions.MAX_LENGTH,
                    multiLine = true,
                )

                TextInput(
                    label = strings.labelLongTerm,
                    value = longTerm,
                    validate = false,
                    maxLength = Ambitions.MAX_LENGTH,
                    multiLine = true,
                )
            }
        }
    }
}
