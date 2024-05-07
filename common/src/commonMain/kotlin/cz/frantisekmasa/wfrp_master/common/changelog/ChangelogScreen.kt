package cz.frantisekmasa.wfrp_master.common.changelog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material.RichText
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.shared.rememberUrlOpener
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.EmptyUI
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.HorizontalLine
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ChangelogScreen : Screen {
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = { BackButton() },
                    title = { Text(stringResource(Str.changelog_title)) },
                )
            },
        ) {
            val screenModel: ChangelogScreenModel = rememberScreenModel()
            val (state, setState) = rememberSaveable { mutableStateOf<State>(State.Loading) }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    val releases = screenModel.loadReleases()

                    setState(
                        if (releases == null) {
                            State.Error
                        } else {
                            State.Loaded(releases)
                        },
                    )
                }
            }

            when (state) {
                State.Loading -> {
                    FullScreenProgress()
                }
                State.Error -> {
                    EmptyUI(
                        text = stringResource(Str.changelog_could_not_load),
                        icon = Icons.Rounded.CloudOff,
                    )
                }
                is State.Loaded -> {
                    val urlOpener = rememberUrlOpener()

                    LazyColumn(
                        contentPadding = PaddingValues(Spacing.bodyPadding),
                    ) {
                        items(state.releases, key = { it.name }) { release ->
                            Column {
                                Text(
                                    release.name,
                                    modifier = Modifier.padding(top = Spacing.medium),
                                    style = MaterialTheme.typography.h5,
                                )

                                RichText(linkClickHandler = { urlOpener.open(it, isGooglePlayLink = false) }) {
                                    Markdown(release.description)
                                }

                                HorizontalLine()
                            }
                        }

                        item("gitlab") {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                TextButton(
                                    onClick = {
                                        urlOpener.open(
                                            "https://github.com/fmasa/wfrp-master/releases",
                                            isGooglePlayLink = false,
                                        )
                                    },
                                ) {
                                    Text(stringResource(Str.changelog_github_button).uppercase())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private sealed interface State : Parcelable {
        @Parcelize
        object Loading : State

        @Parcelize
        object Error : State

        @Immutable
        @Parcelize
        data class Loaded(val releases: List<ChangelogScreenModel.Release>) : State
    }
}
