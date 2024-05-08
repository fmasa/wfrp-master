package cz.frantisekmasa.wfrp_master.common.ambitions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardEditButton
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VISUAL_ONLY_ICON_DESCRIPTION
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun AmbitionsCard(
    modifier: Modifier = Modifier,
    title: String,
    ambitions: Ambitions,
    onSave: (suspend (Ambitions) -> Unit)?,
    titleIcon: ImageVector? = null,
) {
    var dialogOpened by rememberSaveable { mutableStateOf(false) }

    if (dialogOpened && onSave != null) {
        ChangeAmbitionsDialog(
            title = title,
            defaults = ambitions,
            save = onSave,
            onDismissRequest = { dialogOpened = false },
        )
    }

    CardContainer(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(modifier),
        bodyPadding = PaddingValues(start = 8.dp, end = 8.dp),
    ) {
        CardTitle(
            title,
            titleIcon,
            actions =
                if (onSave != null) {
                    ({ CardEditButton(onClick = { dialogOpened = true }) })
                } else {
                    null
                },
        )

        val ambitionList =
            listOf(
                stringResource(Str.ambition_label_short_term) to ambitions.shortTerm,
                stringResource(Str.ambition_label_long_term) to ambitions.longTerm,
            )

        for ((label, value) in ambitionList) {
            Column(Modifier.padding(top = 4.dp)) {
                Text(label, fontWeight = FontWeight.Bold)

                if (value.isBlank()) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                drawableResource(Resources.Drawable.None),
                                VISUAL_ONLY_ICON_DESCRIPTION,
                            )
                            Text(
                                stringResource(Str.ambition_messages_not_filled),
                                style = MaterialTheme.typography.body2,
                                fontStyle = FontStyle.Italic,
                            )
                        }
                    }
                } else {
                    Text(value)
                }
            }
        }
    }
}
