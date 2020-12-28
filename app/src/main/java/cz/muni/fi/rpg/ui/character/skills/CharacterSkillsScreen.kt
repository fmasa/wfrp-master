package cz.muni.fi.rpg.ui.character.skills

import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.core.domain.identifiers.CharacterId
import cz.muni.fi.rpg.ui.character.talents.TalentsCard
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
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
    ScrollableColumn(modifier.background(MaterialTheme.colors.background)) {

        val talentsViewModel: TalentsViewModel by viewModel { parametersOf(characterId) }
        val skillsViewModel: SkillsViewModel by viewModel { parametersOf(characterId) }

        SkillsCard(
            characterVm,
            skillsViewModel,
            onRemove = { skillsViewModel.removeSkill(it) },
        )

        TalentsCard(
            talentsViewModel,
            onRemove = { talentsViewModel.removeTalent(it) },
        )
    }
}
