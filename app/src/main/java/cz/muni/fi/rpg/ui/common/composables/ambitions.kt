package cz.muni.fi.rpg.ui.common.composables

import androidx.compose.foundation.clickable
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
import cz.frantisekmasa.wfrp_master.common.core.domain.Ambitions
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.shared.drawableResource
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardContainer
import cz.frantisekmasa.wfrp_master.common.core.ui.cards.CardTitle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.VisualOnlyIconDescription
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.muni.fi.rpg.ui.common.ChangeAmbitionsDialog

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
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onSave != null)
                    modifier.clickable(onClick = { dialogOpened = true })
                else modifier
            ),
        bodyPadding = PaddingValues(start = 8.dp, end = 8.dp),
    ) {
        CardTitle(title, titleIcon)

        val strings = LocalStrings.current.ambition

        for (
            (label, value) in listOf(
                strings.labelShortTerm to ambitions.shortTerm,
                strings.labelLongTerm to ambitions.longTerm
            )
        ) {
            Column(Modifier.padding(top = 4.dp)) {
                Text(label, fontWeight = FontWeight.Bold)

                if (value.isBlank()) {
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(drawableResource(Resources.Drawable.None), VisualOnlyIconDescription)
                            Text(
                                strings.messages.notFilled,
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
