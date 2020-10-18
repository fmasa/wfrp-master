package cz.muni.fi.rpg.ui.common.composables.dialog

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.muni.fi.rpg.R

@Composable
fun CancelButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(stringResource(R.string.button_cancel).toUpperCase(Locale.current))
    }
}

@Composable
fun SaveButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(stringResource(R.string.button_save).toUpperCase(Locale.current))
    }
}

@Composable
fun Progress() {
    Box(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        alignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}