package cz.frantisekmasa.wfrp_master.common.character.religion.miracles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun MiracleDetail(
    miracle: Miracle,
    onDismissRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CloseButton(onClick = onDismissRequest)
                },
                title = { Text(miracle.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            MiracleDetailBody(
                cultName = miracle.cultName,
                range = miracle.range,
                target = miracle.target,
                duration = miracle.duration,
                effect = miracle.effect,
            )
        }
    }
}

@Composable
fun MiracleDetailBody(
    cultName: String,
    range: String,
    target: String,
    duration: String,
    effect: String,
) {
    Column {
        SubheadBar {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(cultName)
            }
        }

        Column(Modifier.padding(Spacing.bodyPadding)) {
            val strings = LocalStrings.current.miracles

            SingleLineTextValue(strings.labelRange, range)
            SingleLineTextValue(strings.labelTarget, target)
            SingleLineTextValue(strings.labelDuration, duration)

            RichText(Modifier.padding(top = 8.dp)) {
                Markdown(effect)
            }
        }
    }
}
