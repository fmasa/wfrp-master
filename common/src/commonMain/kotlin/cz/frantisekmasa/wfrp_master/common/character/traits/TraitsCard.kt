package cz.frantisekmasa.wfrp_master.common.character.traits

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.characterItemsCard
import cz.frantisekmasa.wfrp_master.common.character.traits.add.AddTraitScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

internal fun LazyListScope.traitsCard(
    characterId: CharacterId,
    traits: ImmutableList<TraitDataItem>,
    onRemove: (TraitDataItem) -> Unit,
) {
    characterItemsCard(
        title = { stringResource(Str.traits_title_traits) },
        key = "traits",
        id = TraitDataItem::id,
        items = traits,
        newItemScreen = { AddTraitScreen(characterId) },
        detailScreen = { trait -> CharacterTraitDetailScreen(characterId, trait.id) },
        onRemove = onRemove,
        item = { trait -> TraitItem(trait) },
    )
}

@Composable
private fun TraitItem(trait: TraitDataItem) {
    ListItem(text = { Text(trait.name) })
}

data class TraitDataItem(
    val id: Uuid,
    val name: String,
)
