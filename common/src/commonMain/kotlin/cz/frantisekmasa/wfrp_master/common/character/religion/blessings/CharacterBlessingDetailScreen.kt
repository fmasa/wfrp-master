package cz.frantisekmasa.wfrp_master.common.character.religion.blessings

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.CharacterItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog.NonCompendiumBlessingForm
import cz.frantisekmasa.wfrp_master.common.compendium.blessing.CompendiumBlessingDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel

class CharacterBlessingDetailScreen(characterId: CharacterId, blessingId: Uuid) :
    CharacterItemDetailScreen(characterId, blessingId) {

    @Composable
    override fun Content() {
        val screenModel: BlessingsScreenModel = rememberScreenModel(arg = characterId)

        Detail(screenModel) { blessing, isGameMaster ->
            val navigation = LocalNavigationTransaction.current

            if (blessing.compendiumId != null) {
                BlessingDetail(
                    blessing = blessing,
                    subheadBar = {
                        if (isGameMaster) {
                            CompendiumButton(
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = Spacing.bodyPadding),
                                onClick = {
                                    navigation.navigate(
                                        CompendiumBlessingDetailScreen(
                                            screenModel.characterId.partyId,
                                            blessing.compendiumId,
                                        )
                                    )
                                }
                            )
                        }
                    },
                    onDismissRequest = navigation::goBack,
                )
            } else {
                NonCompendiumBlessingForm(
                    screenModel = screenModel,
                    existingBlessing = blessing,
                    onDismissRequest = navigation::goBack,
                )
            }
        }
    }
}
