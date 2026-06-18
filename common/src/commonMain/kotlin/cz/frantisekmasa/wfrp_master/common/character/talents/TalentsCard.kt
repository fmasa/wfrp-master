package cz.frantisekmasa.wfrp_master.common.character.talents

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.characterItemsCard
import cz.frantisekmasa.wfrp_master.common.character.talents.add.AddTalentScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

internal fun LazyListScope.talentsCard(
    characterId: CharacterId,
    talents: ImmutableList<TalentDataItem>,
    onRemove: (TalentDataItem) -> Unit,
) {
    characterItemsCard(
        title = { stringResource(Str.talents_title_talents) },
        key = "talents",
        id = TalentDataItem::id,
        items = talents,
        newItemScreen = { AddTalentScreen(characterId) },
        detailScreen = { talent -> CharacterTalentDetailScreen(characterId, talent.id) },
        onRemove = onRemove,
        item = { talent -> TalentItem(talent) },
    )
}

@Composable
private fun TalentItem(talent: TalentDataItem) {
    ListItem(
        text = { Text(talent.name) },
        trailing = { Text("+ ${talent.taken}") },
    )
}

data class TalentDataItem(
    val id: Uuid,
    val name: String,
    val taken: Int,
)
