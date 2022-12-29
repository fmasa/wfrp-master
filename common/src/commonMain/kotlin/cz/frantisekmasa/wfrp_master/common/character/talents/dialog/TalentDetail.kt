package cz.frantisekmasa.wfrp_master.common.character.talents.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun TalentDetail(
    talent: Talent,
    onDismissRequest: () -> Unit,
    subheadBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(talent.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            subheadBar()

            TalentDetailBody(
                maxTimesTaken = null,
                tests = talent.tests,
                description = talent.description,
            )
        }
    }
}

@Composable
fun TalentDetailBody(
    maxTimesTaken: String?,
    tests: String,
    description: String,
) {
    Column(Modifier.padding(Spacing.bodyPadding)) {
        if (maxTimesTaken != null) {
            SingleLineTextValue(LocalStrings.current.talents.labelMaxTimesTaken, maxTimesTaken)
        }

        if (tests.isNotBlank()) {
            SingleLineTextValue(LocalStrings.current.talents.labelTests, tests)
        }

        RichText {
            Markdown(description)
        }
    }
}
