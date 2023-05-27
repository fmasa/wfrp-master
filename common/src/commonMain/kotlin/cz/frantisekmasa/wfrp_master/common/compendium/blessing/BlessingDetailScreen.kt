package cz.frantisekmasa.wfrp_master.common.compendium.blessing

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
class BlessingDetailScreen(
    private val partyId: PartyId,
    private val blessingId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: BlessingCompendiumScreenModel = rememberScreenModel(arg = partyId)

        CompendiumItemDetailScreen(
            id = blessingId,
            screenModel = screenModel,
            detail = {
                BlessingDetailBody(
                    range = it.range,
                    target = it.target,
                    duration = it.duration,
                    effect = it.effect,
                )
            }
        ) { item, onDismissRequest ->
            BlessingDialog(
                blessing = item,
                onDismissRequest = onDismissRequest,
                onSaveRequest = screenModel::update,
            )
        }
    }
}
