package cz.frantisekmasa.wfrp_master.common.compendium.trait

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel

class CompendiumTraitDetailScreen(
    private val partyId: PartyId,
    private val traitId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: TraitCompendiumScreenModel = rememberScreenModel(arg = partyId)

        CompendiumItemDetailScreen(
            id = traitId,
            screenModel = screenModel,
            detail = {
                TraitDetailBody(
                    specifications = it.specifications,
                    description = it.description,
                )
            }
        ) { item, onDismissRequest ->
            TraitDialog(
                trait = item,
                onDismissRequest = onDismissRequest,
                onSaveRequest = screenModel::update,
            )
        }
    }
}
