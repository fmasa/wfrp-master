package cz.frantisekmasa.wfrp_master.common.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.LocalStaticConfiguration
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.localization.FixedStrings
import dev.icerock.moko.resources.compose.stringResource

object AboutScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Str.about_title)) },
                    navigationIcon = { BackButton() },
                )
            },
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 40.dp),
                ) {
                    Text(FixedStrings.APP_NAME, style = MaterialTheme.typography.h4)
                    Text(LocalStaticConfiguration.current.version, Modifier.padding(bottom = 8.dp))
                    Text(stringResource(Str.about_body), textAlign = TextAlign.Center)

                    Divider(Modifier.padding(vertical = 12.dp))

                    Text(
                        stringResource(Str.about_title_attribution),
                        style = MaterialTheme.typography.h6,
                    )

                    listOf(
                        Str.about_attribution_icons,
                        Str.about_attribution_dice_roll_sound,
                    ).forEach {
                        Text(stringResource(it), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
