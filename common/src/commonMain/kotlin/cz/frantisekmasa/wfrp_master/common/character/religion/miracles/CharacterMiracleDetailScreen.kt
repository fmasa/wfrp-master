package cz.frantisekmasa.wfrp_master.common.character.religion.miracles

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog.NonCompendiumMiracleForm
import cz.frantisekmasa.wfrp_master.common.compendium.miracle.CompendiumMiracleDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel

class CharacterMiracleDetailScreen(characterId: CharacterId, miracleId: Uuid) :
    CharacterItemDetailScreen(characterId, miracleId) {

    @Composable
    override fun Content() {
        val screenModel: CharacterMiracleDetailScreenModel = rememberScreenModel(arg = characterId)

        Detail(screenModel) { miracle, isGameMaster ->
            val navigation = LocalNavigationTransaction.current

            if (miracle.compendiumId != null) {
                MiracleDetail(
                    miracle = miracle,
                    subheadBar = {
                        if (isGameMaster) {
                            CompendiumButton(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = Spacing.bodyPadding),
                                onClick = {
                                    navigation.navigate(
                                        CompendiumMiracleDetailScreen(
                                            screenModel.characterId.partyId,
                                            miracle.compendiumId,
                                        )
                                    )
                                }
                            )
                        }
                    },
                    onDismissRequest = navigation::goBack,
                )
            } else {
                NonCompendiumMiracleForm(
                    onSave = screenModel::saveItem,
                    existingMiracle = miracle,
                    onDismissRequest = navigation::goBack,
                )
            }
        }
    }
}
