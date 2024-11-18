package cz.frantisekmasa.wfrp_master.common.character.trappings.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.trappings.detail.journal.TrappingFeatureItem
import cz.frantisekmasa.wfrp_master.common.compendium.journal.rules.TrappingJournal
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Flaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Quality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Rating
import cz.frantisekmasa.wfrp_master.common.core.ui.text.JournalItemList
import cz.frantisekmasa.wfrp_master.common.core.ui.text.SingleLineTextValue
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.toImmutableList

@Composable
fun <TQuality : Quality, TFlaw : Flaw> TrappingFeatures(
    qualities: Map<TQuality, Rating>,
    flaws: Map<TFlaw, Rating>,
    qualityJournalEntries: Map<TQuality, TrappingJournal.Entry>,
    flawJournalEntries: Map<TFlaw, TrappingJournal.Entry>,
) {
    if (qualities.isNotEmpty()) {
        JournalItemList(
            stringResource(Str.trappings_label_qualities),
            qualities.map { (feature, rating) ->
                val journalEntry = qualityJournalEntries.getValue(feature)
                TrappingFeatureItem(
                    feature = feature,
                    rating = rating,
                    journalEntryId = journalEntry.journalEntryId,
                    journalEntryName = journalEntry.journalEntryName,
                    partyId = journalEntry.partyId,
                )
            }.toImmutableList(),
            "quality",
        )
    } else {
        SingleLineTextValue(stringResource(Str.trappings_label_qualities), none())
    }

    if (flaws.isNotEmpty()) {
        JournalItemList(
            stringResource(Str.trappings_label_flaws),
            flaws.map { (feature, rating) ->
                val journalEntry = flawJournalEntries.getValue(feature)
                TrappingFeatureItem(
                    feature = feature,
                    rating = rating,
                    journalEntryId = journalEntry.journalEntryId,
                    journalEntryName = journalEntry.journalEntryName,
                    partyId = journalEntry.partyId,
                )
            }.toImmutableList(),
            "flaw",
        )
    } else {
        SingleLineTextValue(stringResource(Str.trappings_label_flaws), none())
    }
}

@Composable
@Stable
private fun none() =
    AnnotatedString(
        stringResource(Str.trappings_none),
        SpanStyle(fontStyle = FontStyle.Italic),
    )
