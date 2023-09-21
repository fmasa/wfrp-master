package cz.frantisekmasa.wfrp_master.common.core.ui.primitives

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.ContentAlpha
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.IconAction
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.KeyboardEffect
import dev.icerock.moko.resources.compose.stringResource

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
    modifier: Modifier = Modifier,
    data: SearchableList.Data<T>,
    searchableValue: (T) -> String,
    navigationIcon: @Composable () -> Unit,
    title: String,
    searchPlaceholder: String,
    emptyUi: @Composable () -> Unit,
    floatingActionButton: (@Composable () -> Unit)? = null,
    key: (T) -> Any,
    defaultContent: (@Composable () -> Unit)? = null,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var searchedValue by rememberSaveable { mutableStateOf("") }
    var searchActive by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = floatingActionButton ?: {},
        topBar = {
            val searchVisible by derivedStateOf { searchActive || searchedValue != "" }
            var isFocused by remember { mutableStateOf(false) }

            TopAppBar(
                navigationIcon = navigationIcon,
                title = {
                    if (!searchVisible) {
                        Text(title)
                    }

                    val textSelectionColors = TextSelectionColors(
                        handleColor = MaterialTheme.colors.onPrimary,
                        backgroundColor = MaterialTheme.colors.onPrimary.copy(alpha = ContentAlpha.disabled),
                    )
                    CompositionLocalProvider(
                        LocalTextSelectionColors provides textSelectionColors,
                    ) {
                        ProvideTextStyle(MaterialTheme.typography.body1) {
                            TextField(
                                colors = textFieldColors(),
                                value = searchedValue,
                                onValueChange = { searchedValue = it },
                                singleLine = true,
                                placeholder = { Text(searchPlaceholder) },
                                modifier = Modifier
                                    .alpha(if (searchVisible) 1f else 0f)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { isFocused = it.hasFocus }
                            )
                        }
                    }

                    KeyboardEffect("search") {
                        val character = it.utf16CodePoint
                        if (
                            character == 0 ||
                            isFocused || searchedValue != "" ||
                            it.type != KeyEventType.KeyDown
                        ) {
                            return@KeyboardEffect false
                        }

                        val symbol = character.toChar()

                        if (!symbol.isWhitespace() && !symbol.isLetterOrDigit()) {
                            return@KeyboardEffect false
                        }

                        focusRequester.requestFocus()
                        searchedValue += character.toChar()

                        return@KeyboardEffect true
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
                            stringResource(Str.common_ui_button_dismiss),
                            onClick = {
                                searchedValue = ""
                                searchActive = false
                            }
                        )
                    } else {
                        IconAction(
                            Icons.Rounded.Search,
                            stringResource(Str.common_ui_button_dismiss),
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

                if (searchedValue == "" && defaultContent != null) {
                    defaultContent()
                    return@Scaffold
                }

                val filteredItems = remember(searchedValue, searchableValue, items) {
                    if (searchedValue == "")
                        items
                    else items
                        .asSequence()
                        .filter {
                            searchableValue(it).contains(
                                searchedValue,
                                ignoreCase = true
                            )
                        }
                        .sortedByDescending {
                            searchableValue(it).startsWith(searchedValue, ignoreCase = true)
                        }
                        .toList()
                }

                if (filteredItems.isEmpty()) {
                    EmptyUI(
                        text = stringResource(Str.messages_search_not_found),
                        subText = stringResource(Str.messages_search_not_found_subtext),
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
                    items(filteredItems, key = key, itemContent = itemContent)
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
        focusedIndicatorColor = Color.Transparent,
        cursorColor = MaterialTheme.colors.onPrimary,
        placeholderColor = MaterialTheme.colors.onPrimary.copy(ContentAlpha.medium),
        unfocusedIndicatorColor = Color.Transparent,
    )
}
