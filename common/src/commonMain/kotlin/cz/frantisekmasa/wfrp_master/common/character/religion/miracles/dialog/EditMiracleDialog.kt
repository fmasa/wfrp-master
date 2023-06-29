package cz.frantisekmasa.wfrp_master.common.character.religion.miracles.dialog

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiracleDetail
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiraclesScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.miracle.MiracleDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.CompendiumButton
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
internal fun EditMiracleDialog(
    screenModel: MiraclesScreenModel,
    miracleId: Uuid,
    onDismissRequest: () -> Unit
) {
    val miracle =
        screenModel.items.collectWithLifecycle(null)
            .value
            ?.firstOrNull { it.id == miracleId }
            ?: return

    FullScreenDialog(onDismissRequest = onDismissRequest) {
        if (miracle.compendiumId != null) {
            MiracleDetail(
                miracle = miracle,
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
                                    MiracleDetailScreen(
                                        screenModel.characterId.partyId,
                                        miracle.compendiumId,
                                    )
                                )
                            }
                        )
                    }
                },
                onDismissRequest = onDismissRequest,
            )
        } else {
            NonCompendiumMiracleForm(
                screenModel = screenModel,
                existingMiracle = miracle,
                onDismissRequest = onDismissRequest
            )
        }
    }
}
