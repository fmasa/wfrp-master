package cz.frantisekmasa.wfrp_master.common.core.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.interactions.clickableWithoutIndication
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun <T> SelectionDialog(
    title: String,
    items: List<T>,
    selected: T,
    onDismissRequest: () -> Unit,
    onSelect: (T) -> Unit,
    itemContent: @Composable (T) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.medium) {
            var currentItem by remember { mutableStateOf(selected) }

            Column {
                Text(
                    title,
                    Modifier.fillMaxWidth()
                        .padding(
                            horizontal = Spacing.extraLarge,
                            vertical = Spacing.large,
                        ),
                    style = MaterialTheme.typography.h6,
                )

                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                ) {
                    for (item in items) {
                        key(item) {
                            Row(
                                modifier =
                                    Modifier
                                        .padding(horizontal = Spacing.medium)
                                        .clickableWithoutIndication { currentItem = item },
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
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.small, vertical = Spacing.tiny),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = { onSelect(currentItem) }) {
                        Text(stringResource(Str.common_ui_button_save).uppercase())
                    }
                }
            }
        }
    }
}
