package cz.muni.fi.rpg.ui.character.talents.dialog

import androidx.compose.runtime.Composable
import cz.muni.fi.rpg.model.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
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
                onDismissRequest = onDismissRequest
            )
        } else {
            NonCompendiumTalentForm(
                viewModel = viewModel,
                existingTalent = talent,
                onDismissRequest = onDismissRequest
            )
        }
    }
}