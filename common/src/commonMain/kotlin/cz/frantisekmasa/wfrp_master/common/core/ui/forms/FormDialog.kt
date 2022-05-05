package cz.frantisekmasa.wfrp_master.common.core.ui.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.core.shared.IO
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SaveAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun <T> FormDialog(
    title: String,
    onDismissRequest: () -> Unit,
    formData: HydratedFormData<T>,
    onSave: suspend (T) -> Unit,
    content: @Composable ColumnScope.(validate: Boolean) -> Unit,
) {
    FullScreenForm(
        title = title,
        navigationIcon = { CloseButton(onDismissRequest) },
        formData = formData,
        onSave = {
            onSave(it)
            onDismissRequest()
        },
        content = content,
    )
}

@Composable
fun <T> FormScreen(
    title: String,
    formData: HydratedFormData<T>,
    onSave: suspend (T) -> Unit,
    content: @Composable ColumnScope.(validate: Boolean) -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow

    FullScreenForm(
        title = title,
        navigationIcon = { BackButton() },
        formData = formData,
        onSave = {
            onSave(it)
            navigator.pop()
        },
        content = content,
    )
}

@Composable
private fun <T> FullScreenForm(
    title: String,
    navigationIcon: @Composable () -> Unit,
    formData: HydratedFormData<T>,
    onSave: suspend (T) -> Unit,
    content: @Composable ColumnScope.(validate: Boolean) -> Unit,
) {
    var saving by remember { mutableStateOf(false) }
    var validate by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = navigationIcon,
                title = { Text(title) },
                actions = {
                    val coroutineScope = rememberCoroutineScope()

                    SaveAction(
                        enabled = !saving,
                        onClick = {
                            if (!formData.isValid()) {
                                validate = true
                                return@SaveAction
                            }

                            coroutineScope.launch {
                                withContext(Dispatchers.IO) {
                                    saving = true
                                    onSave(formData.toValue())
                                }
                            }
                        }
                    )
                }
            )
        }
    ) {
        if (saving) {
            FullScreenProgress()
            return@Scaffold
        }

        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.bodyPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.small),
        ) {
            content(validate)
        }
    }
}
