package cz.frantisekmasa.wfrp_master.common.core.ui.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            Icons.Rounded.ArrowBack,
            LocalStrings.current.commonUi.labelPreviousScreen,
        )
    }
}

@Composable
fun BackButton() {
    val navigation = LocalNavigationTransaction.current

    BackButton(onClick = navigation::goBack)
}
