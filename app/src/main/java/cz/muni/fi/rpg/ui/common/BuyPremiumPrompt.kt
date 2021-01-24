package cz.muni.fi.rpg.ui.common

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.muni.fi.rpg.R
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import cz.frantisekmasa.wfrp_master.core.viewModel.providePremiumViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun BuyPremiumPrompt(onDismissRequest: () -> Unit) {
    val activity = AmbientActivity.current
    val viewModel = providePremiumViewModel()
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        title = { Text(stringResource(R.string.buy_premium)) },
        onDismissRequest = onDismissRequest,
        text = {
            Text(stringResource(R.string.premium_prompt))
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.button_cancel).toUpperCase(Locale.current))
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
                Text(stringResource(R.string.button_upgrade))
            }
        }
    )
}
