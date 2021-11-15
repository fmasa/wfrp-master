package cz.frantisekmasa.wfrp_master.common.core.ui.components.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Box(Modifier.padding(bottom = Spacing.medium)) {
        Card(shape = MaterialTheme.shapes.large) {
            Column {
                content()
            }
        }
    }
}
