package cz.muni.fi.rpg.ui.character.talents.dialog

import androidx.compose.runtime.Composable
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.muni.fi.rpg.ui.common.composables.dialog.FullScreenDialog
import cz.muni.fi.rpg.viewModels.TalentsViewModel

@Composable
fun EditTalentDialog(
    viewModel: TalentsViewModel,
    talent: Talent,
    onDismissRequest: () -> Unit
) {
    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (talent.compendiumId != null) {
            TimesTakenForm(
                existingTalent = talent,
                compendiumTalentId = talent.compendiumId,
                viewModel = viewModel,
                onComplete = onDismissRequest
            )
        } else {
            NonCompendiumTalentForm(
                viewModel = viewModel,
                existingTalent = talent,
                onComplete = onDismissRequest
            )
        }
    }
}