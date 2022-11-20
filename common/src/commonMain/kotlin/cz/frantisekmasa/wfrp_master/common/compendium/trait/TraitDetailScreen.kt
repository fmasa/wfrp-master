package cz.frantisekmasa.wfrp_master.common.compendium.trait

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId

class TraitDetailScreen(
    private val partyId: PartyId,
    private val traitId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        CompendiumItemDetailScreen(
            partyId = partyId,
            item = { it.getTrait(traitId) },
            detail = {
                TraitDetailBody(
                    specifications = it.specifications,
                    description = it.description,
                )
            }
        ) { item, screenModel, onDismissRequest ->
            TraitDialog(
                trait = item,
                onDismissRequest = onDismissRequest,
                screenModel = screenModel,
            )
        }
    }
}
