package cz.frantisekmasa.wfrp_master.common.compendium.talent

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.TalentDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel

class TalentDetailScreen(
    private val partyId: PartyId,
    private val talentId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: TalentCompendiumScreenModel = rememberScreenModel(arg = partyId)

        CompendiumItemDetailScreen(
            id = talentId,
            screenModel = screenModel,
            detail = {
                TalentDetailBody(
                    maxTimesTaken = it.maxTimesTaken,
                    tests = it.tests,
                    description = it.description,
                )
            }
        ) { item, onDismissRequest ->
            TalentDialog(
                talent = item,
                onDismissRequest = onDismissRequest,
                screenModel = screenModel,
            )
        }
    }
}
