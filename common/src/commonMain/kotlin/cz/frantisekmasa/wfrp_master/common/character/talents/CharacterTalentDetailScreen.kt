package cz.frantisekmasa.wfrp_master.common.character.talents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.NonCompendiumTalentForm
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.TalentDetail
import cz.frantisekmasa.wfrp_master.common.compendium.talent.CompendiumTalentDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.NumberPicker
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.SubheadBar
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CharacterTalentDetailScreen(characterId: CharacterId, talentId: Uuid) :
    CharacterItemDetailScreen(characterId, talentId) {

    @Composable
    override fun Content() {
        val screenModel: TalentsScreenModel = rememberScreenModel(arg = characterId)

        Detail(screenModel) { talent, isGameMaster ->
            val navigation = LocalNavigationTransaction.current

            if (talent.compendiumId != null) {
                val coroutineScope = rememberCoroutineScope()

                TalentDetail(
                    talent = talent,
                    onDismissRequest = navigation::goBack,
                    subheadBar = {
                        TimesTakenBar(talent.taken) { timesTaken ->
                            coroutineScope.launch(Dispatchers.IO) {
                                screenModel.saveTalent(
                                    talent = talent.copy(taken = timesTaken),
                                    existingTalent = talent,
                                )
                            }
                        }

                        if (isGameMaster) {
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
                    onDismissRequest = navigation::goBack,
                )
            }
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
