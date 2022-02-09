package cz.frantisekmasa.wfrp_master.common.core.ui.cards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    bodyPadding: PaddingValues = PaddingValues(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        Modifier
            .padding(vertical = 6.dp)
            .then(modifier)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 2.dp,
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                Modifier
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .padding(bodyPadding),
                content = content
            )
        }
    }
}
