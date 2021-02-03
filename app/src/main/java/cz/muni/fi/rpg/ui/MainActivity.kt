package cz.muni.fi.rpg.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.setContent
import cz.muni.fi.rpg.ui.shell.*

class MainActivity : AppCompatActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProvideActivity(this) {
                WfrpMasterApp()
            }
        }
    }
}