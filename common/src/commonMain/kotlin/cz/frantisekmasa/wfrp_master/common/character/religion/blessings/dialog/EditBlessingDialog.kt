package cz.frantisekmasa.wfrp_master.common.character.religion.blessings.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingDetail
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingsScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.blessing.BlessingDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
internal fun EditBlessingDialog(
    screenModel: BlessingsScreenModel,
    blessingId: Uuid,
    onDismissRequest: () -> Unit
) {
    val blessing = screenModel.items.collectWithLifecycle(null)
        .value
        ?.firstOrNull { it.id == blessingId }
        ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (blessing.compendiumId != null) {
            BlessingDetail(
                blessing = blessing,
                subheadBar = {
                    val isGameMaster = screenModel.isGameMaster.collectWithLifecycle(false).value

                    if (isGameMaster) {
                        val navigation = LocalNavigationTransaction.current

                        CompendiumButton(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = Spacing.bodyPadding),
                            onClick = {
                                navigation.navigate(
                                    BlessingDetailScreen(
                                        screenModel.characterId.partyId,
                                        blessing.compendiumId,
                                    )
                                )
                            }
                        )
                    }
                },
                onDismissRequest = onDismissRequest,
            )
        } else {
            NonCompendiumBlessingForm(
                screenModel = screenModel,
                existingBlessing = blessing,
                onDismissRequest = onDismissRequest
            )
        }
    }
}
