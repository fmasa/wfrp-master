package cz.muni.fi.rpg.ui.character.talents.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import cz.frantisekmasa.wfrp_master.core.ui.dialogs.FullScreenDialog
import cz.muni.fi.rpg.viewModels.TalentsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun EditTalentDialog(
    viewModel: TalentsViewModel,
    talentId: UUID,
    onDismissRequest: () -> Unit
) {
    val talent =
        viewModel.talents.observeAsState().value?.firstOrNull { it.id == talentId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (talent.compendiumId != null) {
            val coroutineScope = rememberCoroutineScope()

            TalentDetail(
                talent = talent,
                onDismissRequest = onDismissRequest,
                onTimesTakenChange = { timesTaken ->
                    coroutineScope.launch(Dispatchers.IO) {
                        viewModel.saveTalent(talent.copy(taken = timesTaken))
                    }
                }
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
