package cz.frantisekmasa.wfrp_master.common.core.ui.text

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.journal.JournalEntryScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.logging.Reporting
import cz.frantisekmasa.wfrp_master.common.core.ui.forms.Chip
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

@Immutable
interface JournalItem {
    val journalEntryId: Uuid?
    val journalEntryName: String
    val key: Any
    val partyId: PartyId

    @Composable
    @Stable
    fun getName(): String
}

@Composable
fun JournalItemList(
    label: String,
    items: ImmutableList<JournalItem>,
    itemType: String,
) {
    if (items.isEmpty()) return

    Column {
        Text(
            "$label:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = Spacing.tiny),
        )
        @OptIn(ExperimentalLayoutApi::class)
        (
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.tiny),
            ) {
                items.forEach { item -> key(item.key) { JournalItemChip(item, itemType) } }
            }
        )
    }
}

@Composable
fun JournalItemChip(
    item: JournalItem,
    itemType: String,
) {
    val navigation = LocalNavigationTransaction.current
    val snackbarHolder = LocalPersistentSnackbarHolder.current

    val notFoundMessage =
        stringResource(
            Str.journal_messages_entry_not_found,
            item.journalEntryName,
        )
    Chip(
        padding = Spacing.tiny,
        modifier =
            Modifier
                .clip(RoundedCornerShape(Spacing.small))
                .clickable {
                    val journalEntryId = item.journalEntryId
                    if (journalEntryId != null) {
                        Reporting.record { journalOpened(itemType) }
                        navigation.navigate(
                            JournalEntryScreen(
                                item.partyId,
                                journalEntryId,
                            ),
                        )
                    } else {
                        snackbarHolder.showSnackbar(notFoundMessage)
                    }
                },
    ) {
        Text(item.getName())
    }
}
