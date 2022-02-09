package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.frantisekmasa.wfrp_master.common.core.ui.interactions.clickableWithoutIndication
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

@Composable
fun <T> SelectionDialog(
    title: String,
    items: List<T>,
    selected: T,
    onDismissRequest: () -> Unit,
    onSelect: (T) -> Unit,
    itemContent: @Composable (T) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            var currentItem by remember { mutableStateOf(selected) }

            Column {
                Box(
                    modifier = Modifier
                        .height(64.dp)
                        .padding(horizontal = Spacing.extraLarge),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        title,
                        Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.h6,
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Spacing.extraLarge)
                ) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier
                                .padding(horizontal = Spacing.extraLarge)
                                .clickableWithoutIndication { currentItem = item },
                            horizontalArrangement = Arrangement.spacedBy(Spacing.extraLarge),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = item == currentItem,
                                onClick = { currentItem = item },
                            )

                            itemContent(item)
                        }
                    }
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = Spacing.medium, bottom = Spacing.small, end = Spacing.small),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { onSelect(currentItem) }) {
                        Text(LocalStrings.current.commonUi.buttonSave.toUpperCase(Locale.current))
                    }
                }
            }
        }
    }
}
