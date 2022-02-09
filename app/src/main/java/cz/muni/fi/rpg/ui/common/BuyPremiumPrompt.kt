package cz.muni.fi.rpg.ui.common

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import cz.frantisekmasa.wfrp_master.common.core.ui.viewinterop.LocalActivity
import cz.frantisekmasa.wfrp_master.common.core.viewModel.providePremiumViewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BuyPremiumPrompt(onDismissRequest: () -> Unit) {
    val activity = LocalActivity.current
    val viewModel = providePremiumViewModel()
    val coroutineScope = rememberCoroutineScope()

    val strings = LocalStrings.current

    AlertDialog(
        title = { Text(strings.premium.dialogTitle) },
        onDismissRequest = onDismissRequest,
        text = {
            Text(strings.premium.prompt)
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(strings.commonUi.buttonCancel.uppercase())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.purchasePremium(activity)
                    }
                },
            ) {
                Text(strings.premium.buttonUpgrade.uppercase())
            }
        }
    )
}
