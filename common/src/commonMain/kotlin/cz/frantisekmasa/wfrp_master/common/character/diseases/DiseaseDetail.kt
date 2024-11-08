package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.text.JournalItem
import cz.frantisekmasa.wfrp_master.common.core.ui.text.JournalItemList
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

@Composable
fun DiseaseDetailBody(
    subheadBar: @Composable ColumnScope.() -> Unit = {},
    symptoms: ImmutableList<Symptom>,
    permanentEffects: String,
    description: String,
) {
    Column(Modifier.padding(Spacing.bodyPadding)) {
        subheadBar()

        JournalItemList(
            label = stringResource(Str.diseases_label_symptoms),
            itemType = "disease_symptom",
            items = symptoms,
        )

        SingleLineTextValue(
            label = stringResource(Str.diseases_label_permanent_effects),
            value = permanentEffects,
        )

        RichText(Modifier.padding(top = Spacing.small)) {
            Markdown(description)
        }
    }
}

data class Symptom(
    private val name: String,
    override val journalEntryId: Uuid?,
    override val journalEntryName: String,
    override val partyId: PartyId,
) : JournalItem {
    override val key = name

    @Composable
    override fun getName(): String {
        return name
    }
}
