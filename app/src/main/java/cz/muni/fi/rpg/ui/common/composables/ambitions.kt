package cz.muni.fi.rpg.ui.common.composables

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.muni.fi.rpg.R
import cz.muni.fi.rpg.model.domain.common.Ambitions

@Composable
fun AmbitionsCard(
    @StringRes titleRes: Int,
    ambitions: Ambitions,
    @DrawableRes titleIconRes: Int? = null,
    modifier: Modifier = Modifier,
) {
    CardContainer(Modifier.fillMaxWidth().then(modifier)) {
        Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            CardTitle(titleRes, titleIconRes)

            for ((label, value) in listOf(
                R.string.label_ambition_short_term to ambitions.shortTerm,
                R.string.label_ambition_long_term to ambitions.longTerm
            )) {
                Column(Modifier.padding(top = 4.dp)) {
                    Text(
                        stringResource(label),
                        fontWeight = FontWeight.Bold
                    )

                    if (value.isBlank()) {
                        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(vectorResource(R.drawable.ic_none))
                                Text(
                                    stringResource(R.string.note_ambition_not_filled),
                                    style = MaterialTheme.typography.body2,
                                    fontStyle = FontStyle.Italic,
                                )
                            }
                        }
                    } else {
                        Text(ambitions.shortTerm)
                    }
                }
            }
        }
    }
}