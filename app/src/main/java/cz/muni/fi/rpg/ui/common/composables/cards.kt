package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CardTitle(@StringRes textRes: Int) {
    Text(
        stringResource(textRes), style = MaterialTheme.typography.h6, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CardButton(@StringRes textRes: Int, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        Modifier.padding(top = 16.dp).gravity(Alignment.CenterHorizontally)
    ) {
        Text(stringResource(textRes))
    }
}