package cz.frantisekmasa.wfrp_master.common.compendium.talent

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.TalentDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId

class TalentDetailScreen(
    private val partyId: PartyId,
    private val talentId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        CompendiumItemDetailScreen(
            partyId = partyId,
            item = { it.getTalent(talentId) },
            detail = {
                TalentDetailBody(
                    maxTimesTaken = it.maxTimesTaken,
                    description = it.description,
                )
            }
        ) { item, screenModel, onDismissRequest ->
            TalentDialog(
                talent = item,
                onDismissRequest = onDismissRequest,
                screenModel = screenModel,
            )
        }
    }
}
