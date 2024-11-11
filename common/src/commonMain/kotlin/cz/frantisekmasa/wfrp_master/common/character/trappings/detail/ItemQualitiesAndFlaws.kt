package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.journal.TrappingFeatureItem
import cz.frantisekmasa.wfrp_master.common.compendium.journal.rules.TrappingJournal
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.InventoryItem
import cz.frantisekmasa.wfrp_master.common.core.ui.text.JournalItemList
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ItemQualitiesAndFlaws(
    trapping: InventoryItem,
    trappingJournal: TrappingJournal,
) {
    Column {
        JournalItemList(
            label = stringResource(Str.trappings_label_item_qualities),
            items =
                trapping.itemQualities.map { quality ->
                    val journalEntry = trappingJournal.itemQualities.getValue(quality)
                    TrappingFeatureItem(
                        feature = quality,
                        rating = 1,
                        journalEntryId = journalEntry.journalEntryId,
                        journalEntryName = journalEntry.journalEntryName,
                        partyId = journalEntry.partyId,
                    )
                }.toImmutableList(),
            itemType = "item_quality",
        )

        JournalItemList(
            label = stringResource(Str.trappings_label_item_flaws),
            items =
                trapping.itemFlaws.map { flaw ->
                    val journalEntry = trappingJournal.itemFlaws.getValue(flaw)
                    TrappingFeatureItem(
                        feature = flaw,
                        rating = 1,
                        journalEntryId = journalEntry.journalEntryId,
                        journalEntryName = journalEntry.journalEntryName,
                        partyId = journalEntry.partyId,
                    )
                }.toImmutableList(),
            itemType = "item_flaw",
        )
    }
}
