package cz.frantisekmasa.wfrp_master.core.ui.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import cz.frantisekmasa.wfrp_master.core.R

@Composable
fun CloseButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painterResource(R.drawable.ic_close),
            stringResource(R.string.icon_close_dialog),
        )
    }
}