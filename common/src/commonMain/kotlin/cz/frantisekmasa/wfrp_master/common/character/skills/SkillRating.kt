package cz.frantisekmasa.wfrp_master.common.character.skills

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun SkillRating(
    label: String,
    value: Int,
    modifier: Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(label)
        Text(
            value.toString(),
            style = MaterialTheme.typography.h3,
        )
    }
}
