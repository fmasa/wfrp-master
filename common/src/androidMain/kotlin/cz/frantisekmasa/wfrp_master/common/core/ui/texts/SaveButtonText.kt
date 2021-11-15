package cz.frantisekmasa.wfrp_master.common.core.ui.texts

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun SaveButtonText() = Text(LocalStrings.current.commonUi.buttonSave.toUpperCase(Locale.current))
