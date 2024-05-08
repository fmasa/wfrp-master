package cz.frantisekmasa.wfrp_master.common.character.trappings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.sum
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.menu.WithContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ContextMenu
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun TrappingItem(
    trapping: TrappingItem,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onRemove: () -> Unit,
    additionalContextItems: List<ContextMenu.Item> = emptyList(),
) {
    WithContextMenu(
        onClick = onClick,
        items =
            buildList {
                addAll(additionalContextItems)

                add(
                    ContextMenu.Item(
                        stringResource(Str.common_ui_button_duplicate),
                        onClick = onDuplicate,
                    ),
                )

                add(
                    ContextMenu.Item(
                        stringResource(Str.common_ui_button_remove),
                        onClick = onRemove,
                    ),
                )
            },
    ) {
        TrappingItem(trapping)
    }
}

@Composable
fun TrappingItem(
    trapping: TrappingItem,
    modifier: Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        icon = { ItemIcon(trappingIcon(trapping.item.trappingType), ItemIcon.Size.Small) },
        text = {
            Text(
                buildString {
                    append(trapping.item.name)

                    val itemFeatures = trapping.item.itemQualities + trapping.item.itemFlaws

                    if (trapping.item.compendiumId != null && itemFeatures.isNotEmpty()) {
                        val featureNames = itemFeatures.map { it.localizedName }
                        val sorted = remember(featureNames) { featureNames.sorted() }

                        append(" (")
                        append(sorted.joinToString(", "))
                        append(')')
                    }
                },
            )
        },
        trailing = {
            Column(horizontalAlignment = Alignment.End) {
                if (trapping.item.quantity > 1) {
                    Text("× ${trapping.item.quantity}")
                }

                val encumbrance = trapping.item.effectiveEncumbrance

                if (encumbrance != Encumbrance.Zero) {
                    IconWithValue(
                        Resources.Drawable.TrappingEncumbrance,
                        encumbrance.toString(),
                    )
                }

                if (trapping is TrappingItem.Container) {
                    val currentlyCarries by derivedStateOf {
                        trapping.storedTrappings
                            .map { it.totalEncumbrance }
                            .sum()
                    }

                    IconWithValue(
                        Resources.Drawable.TrappingContainer,
                        trapping.storedTrappings.size.toString(),
                        textColor =
                            if (currentlyCarries > trapping.container.carries) {
                                MaterialTheme.colors.error
                            } else {
                                LocalContentColor.current
                            },
                    )
                }
            }
        },
    )
}

@Composable
private fun IconWithValue(
    icon: Resources.Drawable,
    value: String,
    textColor: Color = LocalContentColor.current,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.tiny),
    ) {
        Icon(
            drawableResource(icon),
            stringResource(Str.trappings_icon_encumbrance),
            Modifier.size(Spacing.medium),
        )
        Text(value, color = textColor)
    }
}
