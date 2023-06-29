package cz.frantisekmasa.wfrp_master.common.compendium.skill

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.SkillDetailBody
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel

class CompendiumSkillDetailScreen(
    private val partyId: PartyId,
    private val skillId: Uuid,
) : Screen {

    @Composable
    override fun Content() {
        val screenModel: SkillCompendiumScreenModel = rememberScreenModel(arg = partyId)

        CompendiumItemDetailScreen(
            id = skillId,
            screenModel = screenModel,
            detail = {
                SkillDetailBody(
                    characteristic = it.characteristic,
                    advanced = it.advanced,
                    description = it.description,
                )
            }
        ) { item, onDismissRequest ->
            SkillDialog(
                skill = item,
                onDismissRequest = onDismissRequest,
                onSaveRequest = screenModel::update,
            )
        }
    }
}
