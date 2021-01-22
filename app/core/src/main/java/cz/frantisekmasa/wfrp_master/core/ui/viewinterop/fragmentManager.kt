package cz.frantisekmasa.wfrp_master.core.ui.viewinterop

import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager

@Composable
fun fragmentManager() : FragmentManager {
    return AmbientActivity.current.supportFragmentManager
}