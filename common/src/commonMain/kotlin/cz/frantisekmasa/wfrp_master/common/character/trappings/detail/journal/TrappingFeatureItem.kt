package cz.frantisekmasa.wfrp_master.common.character.trappings.detail.journal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Rating
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingFeature
import cz.frantisekmasa.wfrp_master.common.core.ui.text.JournalItem

@Immutable
data class TrappingFeatureItem<T : TrappingFeature>(
    val feature: T,
    val rating: Rating,
    override val journalEntryId: Uuid?,
    override val journalEntryName: String,
    override val partyId: PartyId,
) : JournalItem {
    override val key = feature

    @Composable
    override fun getName(): String {
        return feature.formatValue(rating)
    }
}
