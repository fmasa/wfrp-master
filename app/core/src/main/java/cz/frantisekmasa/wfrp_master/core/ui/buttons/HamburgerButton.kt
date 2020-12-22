package cz.frantisekmasa.wfrp_master.core.ui.buttons

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.vectorResource
import androidx.drawerlayout.widget.DrawerLayout
import cz.frantisekmasa.wfrp_master.core.R

@Composable
fun HamburgerButton() {
    val context = AmbientContext.current
    require(context is AppCompatActivity)

    IconButton(onClick = {
        TODO()
//        context.findViewById<DrawerLayout>(R.id.drawer_layout)?.open()
    }) {
        Icon(vectorResource(R.drawable.ic_menu))
    }
}