package cz.frantisekmasa.wfrp_master.common.compendium.disease

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.diseases.DiseaseDetailBody
import cz.frantisekmasa.wfrp_master.common.character.diseases.Symptom
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreen
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemDetailScreenState
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineMarkdownValue
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

class CompendiumDiseaseDetailScreen(
    private val partyId: PartyId,
    private val diseaseId: Uuid,
) : Screen {
    @Composable
    override fun Content() {
        val screenModel: DiseaseCompendiumScreenModel = rememberScreenModel(arg = partyId)

        CompendiumItemDetailScreen(
            itemFlow = remember(screenModel) { screenModel.getDiseaseDetail(diseaseId) },
            screenModel = screenModel,
            detail = { DiseaseDetail(it) },
        ) { item, onDismissRequest ->
            DiseaseDialog(
                disease = item,
                onDismissRequest = onDismissRequest,
                onSaveRequest = screenModel::update,
            )
        }
    }

    @Composable
    private fun DiseaseDetail(state: CompendiumDiseaseDetailScreenState) {
        DiseaseDetailBody(
            subheadBar = {
                SingleLineMarkdownValue(
                    label = stringResource(Str.diseases_label_contraction),
                    value = state.item.contraction,
                )
                SingleLineTextValue(
                    label = stringResource(Str.diseases_label_incubation),
                    state.item.incubation,
                )
                SingleLineMarkdownValue(
                    label = stringResource(Str.diseases_label_duration),
                    state.item.duration,
                )
            },
            symptoms = state.symptoms,
            permanentEffects = state.item.permanentEffects,
            description = state.item.description,
            partyId = partyId,
        )
    }
}

data class CompendiumDiseaseDetailScreenState(
    override val item: Disease,
    val symptoms: ImmutableList<Symptom>,
) : CompendiumItemDetailScreenState<Disease>
