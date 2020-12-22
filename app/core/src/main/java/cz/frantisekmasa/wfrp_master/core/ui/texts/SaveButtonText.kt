package cz.frantisekmasa.wfrp_master.core.ui.texts

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.frantisekmasa.wfrp_master.core.R

@Composable
fun SaveButtonText() = Text(stringResource(R.string.button_save).toUpperCase(Locale.current))
