package cz.frantisekmasa.wfrp_master.common.character.skills.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CloseButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SkillDetail(
    skill: Skill,
    onDismissRequest: () -> Unit,
    subheadBar: @Composable ColumnScope.() -> Unit = {},
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
        SingleLineTextValue(
            label = stringResource(Str.skills_label_characteristic),
            characteristic.localizedName,
        )

        SingleLineTextValue(
            label = stringResource(Str.skills_label_advanced),
            value = stringResource(
                if (advanced)
                    Str.common_ui_boolean_yes
                else Str.common_ui_boolean_no,
            ),
        )

        RichText(Modifier.padding(top = Spacing.small)) {
            Markdown(description)
        }
    }
}
