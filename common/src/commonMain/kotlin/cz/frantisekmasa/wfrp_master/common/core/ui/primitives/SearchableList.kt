package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings


object SearchableList {
    @Immutable
    sealed interface Data<out T> {

        @Immutable
        data class Loaded<T>(val items: List<T>) : Data<T>

        @Immutable
        object Loading : Data<Nothing>
    }
}

@Composable
fun <T : Any> SearchableList(
    data: SearchableList.Data<T>,
    searchableValue: (T) -> String,
    navigationIcon: @Composable () -> Unit,
    title: String,
    searchPlaceholder: String,
    emptyUi: @Composable () -> Unit,
    floatingActionButton: (@Composable () -> Unit)? = null,
    key: (T) -> Any,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var searchedValue by rememberSaveable { mutableStateOf("") }
    var searchActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            val searchVisible by derivedStateOf { searchActive || searchedValue != "" }

            TopAppBar(
                navigationIcon = navigationIcon,
                title = {
                    if (searchVisible) {
                        ProvideTextStyle(MaterialTheme.typography.body1) {
                            TextField(
                                colors = textFieldColors(),
                                value = searchedValue,
                                onValueChange = { searchedValue = it },
                                singleLine = true,
                                placeholder = { Text(searchPlaceholder) },
                                modifier = Modifier.focusRequester(focusRequester),
                            )
                        }
                    } else {
                        Text(title)
                    }

                    DisposableEffect(searchVisible) {
                        if (searchVisible) {
                            focusRequester.requestFocus()
                        }

                        onDispose { }
                    }
                },
                actions = {
                    if (searchActive) {
                        IconAction(
                            Icons.Rounded.Close,
                            LocalStrings.current.commonUi.buttonDismiss,
                            onClick = {
                                searchedValue = ""
                                searchActive = false
                            }
                        )
                    } else {
                        IconAction(
                            Icons.Rounded.Search,
                            LocalStrings.current.commonUi.buttonDismiss,
                            onClick = { searchActive = true }
                        )
                    }
                },
            )
        }
    ) {
        when (data) {
            SearchableList.Data.Loading -> {
                FullScreenProgress()
                return@Scaffold
            }
            is SearchableList.Data.Loaded -> {
                val items by derivedStateOf { data.items }

                if (items.isEmpty()) {
                    emptyUi()
                    return@Scaffold
                }

                val filteredItems by derivedStateOf {
                    if (searchedValue == "")
                        items
                    else items.filter {
                        searchableValue(it).contains(
                            searchedValue,
                            ignoreCase = true
                        )
                    }
                }

                if (filteredItems.isEmpty()) {
                    EmptyUI(
                        text = LocalStrings.current.messages.searchNotFound,
                        subText = LocalStrings.current.messages.searchNotFoundSubtext,
                        icon = Icons.Rounded.SearchOff,
                    )
                    return@Scaffold
                }

                LazyColumn(
                    contentPadding = PaddingValues(
                        top = Spacing.medium,
                        bottom = if (floatingActionButton != null)
                            Spacing.bottomPaddingUnderFab
                        else Spacing.medium,
                    ),
                ) {
                    items(filteredItems, key = key, itemContent)
                }
            }
        }
    }
}

@Stable
@Composable
private fun textFieldColors(): TextFieldColors {
    return TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        textColor = MaterialTheme.colors.onPrimary,
        placeholderColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.7f),
        cursorColor = MaterialTheme.colors.onPrimary,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )
}
