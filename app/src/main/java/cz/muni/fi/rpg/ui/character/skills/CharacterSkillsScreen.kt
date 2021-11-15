package cz.muni.fi.rpg.ui.character.skills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.components.Breakpoint
import cz.frantisekmasa.wfrp_master.common.core.ui.components.ColumnSize.FullWidth
import cz.frantisekmasa.wfrp_master.common.core.ui.components.ColumnSize.HalfWidth
import cz.frantisekmasa.wfrp_master.common.core.ui.components.Container
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.muni.fi.rpg.ui.character.talents.TalentsCard
import cz.muni.fi.rpg.viewModels.CharacterViewModel
import cz.muni.fi.rpg.viewModels.SkillsViewModel
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CharacterSkillsScreen(
    modifier: Modifier = Modifier,
    characterId: CharacterId,
    characterVm: CharacterViewModel,
) {
    Container(
        modifier
            .background(MaterialTheme.colors.background)
            .verticalScroll(rememberScrollState())
            .padding(top = Spacing.small),
    ) {

        val size = if (breakpoint > Breakpoint.XSmall) HalfWidth else FullWidth

        column(size) {
            val skillsViewModel: SkillsViewModel by viewModel { parametersOf(characterId) }

            SkillsCard(
                characterVm,
                skillsViewModel,
                onRemove = { skillsViewModel.removeSkill(it) },
            )
        }

        column(size) {
            val talentsViewModel: TalentsViewModel by viewModel { parametersOf(characterId) }
            TalentsCard(
                talentsViewModel,
                onRemove = { talentsViewModel.removeTalent(it) },
            )
        }
    }
}
