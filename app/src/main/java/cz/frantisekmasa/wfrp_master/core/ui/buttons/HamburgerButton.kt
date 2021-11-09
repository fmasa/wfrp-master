package cz.frantisekmasa.wfrp_master.core.ui.buttons

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cz.muni.fi.rpg.R

@Composable
fun HamburgerButton() {
    val callback = LocalHamburgerButtonHandler.current
    IconButton(onClick = callback) {
        Icon(
            painterResource(R.drawable.ic_menu),
            stringResource(R.string.icon_hamburger),
        )
    }
}
