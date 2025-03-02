package cz.frantisekmasa.wfrp_master.common.core.ui.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.AutoMirrored.Rounded.ArrowBack,
            stringResource(Str.common_ui_label_previous_screen),
        )
    }
}

@Composable
fun BackButton() {
    val navigation = LocalNavigationTransaction.current

    BackButton(onClick = navigation::goBack)
}
