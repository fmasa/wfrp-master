package cz.frantisekmasa.wfrp_master.common.character.diseases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Chip
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.benasher44.uuid.Uuid
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalEntryScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

@Composable
fun DiseaseDetailBody(
    subheadBar: @Composable ColumnScope.() -> Unit = {},
    partyId: PartyId,
    symptoms: ImmutableList<Symptom>,
    permanentEffects: String,
    description: String,
) {
    Column(Modifier.padding(Spacing.bodyPadding)) {
        subheadBar()

        SymptomList(
            partyId = partyId,
            symptoms = symptoms,
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
    val name: String,
    val journalEntryId: Uuid?,
    val journalEntryName: String,
)

@Composable
private fun SymptomList(
    partyId: PartyId,
    symptoms: ImmutableList<Symptom>,
) {
    if (symptoms.isEmpty()) return

    Column {
        Text(
            "${stringResource(Str.diseases_label_symptoms)}:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = Spacing.tiny),
        )
        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.tiny),
        ) {
            val navigation = LocalNavigationTransaction.current
            val snackbarHolder = LocalPersistentSnackbarHolder.current

            symptoms.forEach { symptom ->
                key(symptom.name) {
                    val notFoundMessage = stringResource(Str.journal_messages_entry_not_found, symptom.journalEntryName)
                    Chip(
                        onClick = {
                            if (symptom.journalEntryId != null) {
                                Reporting.record { journalOpened("disease_symptom") }
                                navigation.navigate(JournalEntryScreen(partyId, symptom.journalEntryId))
                            } else {
                                snackbarHolder.showSnackbar(notFoundMessage)
                            }
                        },
                    ) {
                        Text(symptom.name)
                    }
                }
            }
        }
    }
}
