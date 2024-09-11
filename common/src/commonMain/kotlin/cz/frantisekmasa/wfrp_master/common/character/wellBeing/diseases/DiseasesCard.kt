package cz.frantisekmasa.wfrp_master.common.character.wellBeing.diseases

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.characterItemsCard
import cz.frantisekmasa.wfrp_master.common.character.diseases.CharacterDiseaseDetailScreen
import cz.frantisekmasa.wfrp_master.common.core.domain.character.Countdown
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.collections.immutable.ImmutableList

fun LazyListScope.diseasesCard(
    characterId: CharacterId,
    diseases: ImmutableList<DiseaseItem>,
    onRemoveRequest: (DiseaseItem) -> Unit,
) {
    characterItemsCard(
        title = { stringResource(Str.diseases_title_diseases) },
        leadingDivider = true,
        key = "diseases",
        id = DiseaseItem::id,
        items = diseases,
        newItemScreen = { AddDiseaseScreen(characterId) },
        detailScreen = { disease -> CharacterDiseaseDetailScreen(characterId, disease.id) },
        onRemove = onRemoveRequest,
        item = { disease ->
            ListItem(
                text = { Text(disease.name) },
                icon = { ItemIcon(Resources.Drawable.Disease) },
                secondaryText =
                    if (!disease.isDiagnosed) {
                        (
                            {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(Spacing.tiny),
                                ) {
                                    Icon(
                                        Icons.Rounded.VisibilityOff,
                                        stringResource(Str.diseases_messages_visible_to_player_false),
                                        Modifier.height(Spacing.large),
                                    )
                                    Text(
                                        stringResource(Str.diseases_label_not_diagnosed),
                                    )
                                }
                            }
                        )
                    } else {
                        null
                    },
                trailing = {
                    when {
                        disease.isHealed -> {
                            Text(stringResource(Str.diseases_label_healed))
                        }

                        disease.incubation.value > 0 -> {
                            Time(
                                stringResource(Str.diseases_label_incubation),
                                disease.incubation,
                            )
                        }

                        else -> {
                            Time(
                                stringResource(Str.diseases_label_duration),
                                disease.duration,
                            )
                        }
                    }
                },
            )
        },
    )
}

@Composable
private fun Time(
    label: String,
    value: Countdown,
) {
    Column(horizontalAlignment = Alignment.End) {
        Text("$label:")
        Text(value.toText())
    }
}

data class DiseaseItem(
    val id: Uuid,
    val name: String,
    val incubation: Countdown,
    val duration: Countdown,
    val isDiagnosed: Boolean,
    val isHealed: Boolean,
)
