package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

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
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun SkillDetail(
    skill: Skill,
    onDismissRequest: () -> Unit,
    subheadBar: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onDismissRequest) },
                title = { Text(skill.name) },
            )
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            subheadBar()

            SkillDetailBody(
                characteristic = skill.characteristic,
                advanced = skill.advanced,
                description = skill.description,
            )
        }
    }
}

@Composable
fun SkillDetailBody(
    characteristic: Characteristic,
    advanced: Boolean,
    description: String,
) {
    Column(Modifier.padding(Spacing.bodyPadding)) {
        val strings = LocalStrings.current

        SingleLineTextValue(
            label = strings.skills.labelCharacteristic,
            characteristic.localizedName,
        )

        SingleLineTextValue(
            label = strings.skills.labelAdvanced,
            value = strings.commonUi.boolean(advanced),
        )

        RichText(Modifier.padding(top = Spacing.small)) {
            Markdown(description)
        }
    }
}
