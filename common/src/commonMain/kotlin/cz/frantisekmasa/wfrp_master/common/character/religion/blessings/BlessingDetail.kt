package cz.frantisekmasa.wfrp_master.common.character.religion.blessings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun BlessingDetail(
    blessing: Blessing,
    onDismissRequest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    CloseButton(onClick = onDismissRequest)
                },
                title = { Text(blessing.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            BlessingDetailBody(
                range = blessing.range,
                target = blessing.target,
                duration = blessing.duration,
                effect = blessing.effect,
            )
        }
    }
}

@Composable
fun BlessingDetailBody(
    range: String,
    target: String,
    duration: String,
    effect: String,
) {
    Column(Modifier.padding(Spacing.bodyPadding)) {
        val strings = LocalStrings.current.blessings

        SingleLineTextValue(strings.labelRange, range)
        SingleLineTextValue(strings.labelTarget, target)
        SingleLineTextValue(strings.labelDuration, duration)

        RichText(Modifier.padding(top = 8.dp)) {
            Markdown(effect)
        }
    }
}
