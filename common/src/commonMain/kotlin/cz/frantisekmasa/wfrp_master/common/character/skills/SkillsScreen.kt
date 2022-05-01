package cz.frantisekmasa.wfrp_master.common.character.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.character.CharacterScreenModel
import cz.frantisekmasa.wfrp_master.common.character.talents.TalentsCard
import cz.frantisekmasa.wfrp_master.common.character.talents.TalentsScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Breakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.ColumnSize
import cz.frantisekmasa.wfrp_master.common.core.ui.responsive.Container


@Composable
fun SkillsScreen(
    modifier: Modifier = Modifier,
    screenModel: CharacterScreenModel,
    skillsScreenModel: SkillsScreenModel,
    talentsScreenModel: TalentsScreenModel,
) {
    Container(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small),
    ) {

        val size = if (breakpoint > Breakpoint.XSmall) ColumnSize.HalfWidth else ColumnSize.FullWidth

        column(size) {
            SkillsCard(
                screenModel,
                skillsScreenModel,
                onRemove = { skillsScreenModel.removeSkill(it) },
            )
        }

        column(size) {
            TalentsCard(
                talentsScreenModel,
                onRemove = { talentsScreenModel.removeTalent(it) },
            )
        }
    }
}
