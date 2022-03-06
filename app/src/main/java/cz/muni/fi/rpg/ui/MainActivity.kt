package cz.muni.fi.rpg.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.config.StaticConfiguration
import cz.muni.fi.rpg.BuildConfig
import cz.muni.fi.rpg.ui.shell.ProvideActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val configuration = StaticConfiguration(
            isProduction = !BuildConfig.DEBUG,
        )

        setContent {
            CompositionLocalProvider(
                LocalStaticConfiguration provides configuration
            ) {
                ProvideActivity(this) {
                    WfrpMasterApp()
                }
            }
        }
    }
}
