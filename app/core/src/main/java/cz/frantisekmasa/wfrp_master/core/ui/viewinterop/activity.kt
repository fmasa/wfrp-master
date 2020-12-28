package cz.frantisekmasa.wfrp_master.core.ui.viewinterop

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext

@Composable
fun activity() : AppCompatActivity {
    val context = AmbientContext.current

    check(context is AppCompatActivity)

    return context
}