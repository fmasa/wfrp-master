package cz.frantisekmasa.wfrp_master.common.compendium.miracle

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiracleDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel

class MiracleDetailScreen(
    private val partyId: PartyId,
    private val miracleId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: MiracleCompendiumScreenModel = rememberScreenModel(arg = partyId)

        CompendiumItemDetailScreen(
            id = miracleId,
            screenModel = screenModel,
            detail = {
                MiracleDetailBody(
                    cultName = it.cultName,
                    range = it.range,
                    target = it.target,
                    duration = it.duration,
                    effect = it.effect,
                )
            }
        ) { item, onDismissRequest ->
            MiracleDialog(
                miracle = item,
                onDismissRequest = onDismissRequest,
                screenModel = screenModel,
            )
        }
    }
}
