package cz.frantisekmasa.wfrp_master.common.character.talents.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.talents.TalentsScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.talent.CompendiumTalentDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EditTalentDialog(
    screenModel: TalentsScreenModel,
    talentId: Uuid,
    onDismissRequest: () -> Unit
) {
    val talent =
        screenModel.items.collectWithLifecycle(null)
            .value
            ?.firstOrNull { it.id == talentId } ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (talent.compendiumId != null) {
            val coroutineScope = rememberCoroutineScope()

            TalentDetail(
                talent = talent,
                onDismissRequest = onDismissRequest,
                subheadBar = {
                    TimesTakenBar(talent.taken) { timesTaken ->
                        coroutineScope.launch(Dispatchers.IO) {
                            screenModel.saveTalent(talent.copy(taken = timesTaken))
                        }
                    }

                    val isGameMaster = screenModel.isGameMaster.collectWithLifecycle(false).value

                    if (isGameMaster) {
                        val navigation = LocalNavigationTransaction.current

                        CompendiumButton(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = Spacing.bodyPadding),
                            onClick = {
                                navigation.navigate(
                                    CompendiumTalentDetailScreen(
                                        screenModel.characterId.partyId,
                                        talent.compendiumId,
                                    )
                                )
                            }
                        )
                    }
                },
            )
        } else {
            NonCompendiumTalentForm(
                screenModel = screenModel,
                existingTalent = talent,
                onDismissRequest = onDismissRequest,
            )
        }
    }
}

@Composable
private fun TimesTakenBar(timesTaken: Int, onTimesTakenChange: (timesTaken: Int) -> Unit) {
    SubheadBar {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = LocalStrings.current.talents.labelTimesTaken,
                modifier = Modifier.weight(1f),
            )
            NumberPicker(
                value = timesTaken,
                onIncrement = { onTimesTakenChange(timesTaken + 1) },
                onDecrement = {
                    if (timesTaken > 1) {
                        onTimesTakenChange(timesTaken - 1)
                    }
                }
            )
        }
    }
}
