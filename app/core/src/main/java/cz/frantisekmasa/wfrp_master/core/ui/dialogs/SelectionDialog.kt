package cz.frantisekmasa.wfrp_master.core.ui.dialogs

import androidx.compose.foundation.InteractionState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.frantisekmasa.wfrp_master.core.R
import cz.frantisekmasa.wfrp_master.core.ui.primitives.Spacing

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
                                .clickable(
                                    interactionState = remember { InteractionState() },
                                    indication = null
                                ) { currentItem = item },
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
                        Text(stringResource(R.string.button_save).toUpperCase(Locale.current))
                    }
                }
            }
        }
    }
}

