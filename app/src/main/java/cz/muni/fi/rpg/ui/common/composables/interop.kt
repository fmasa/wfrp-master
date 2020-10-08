package cz.muni.fi.rpg.ui.common.composables

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ContextAmbient
import androidx.fragment.app.FragmentManager

@Composable
fun fragmentManager() : FragmentManager {
    return activity().supportFragmentManager
}

@Composable
fun activity() : AppCompatActivity {
    val context = ContextAmbient.current

    check(context is AppCompatActivity)

    return context
}