package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.FileType
import cz.frantisekmasa.wfrp_master.common.core.shared.rememberFileSaver
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.FullScreenProgress
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier

class JsonCompendiumExportScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        val screenModel: CompendiumExportScreenModel = rememberScreenModel(arg = partyId)
        val party = screenModel.party.collectWithLifecycle(null).value

        if (party == null) {
            FullScreenProgress()
            return
        }

        Scaffold(topBar = { TopBar(party.name) }) {
            MainContainer(party.name, screenModel)
        }
    }

    @Composable
    private fun TopBar(partyName: String) {
        TopAppBar(
            title = {
                Column {
                    Text(stringResource(Str.compendium_title_export_compendium))
                    Subtitle(partyName)
                }
            },
            navigationIcon = { BackButton() },
        )
    }

    @Composable
    private fun MainContainer(partyName: String, screenModel: CompendiumExportScreenModel) {
        val snackbarHolder = LocalPersistentSnackbarHolder.current
        var exporting by remember { mutableStateOf(false) }

        val messageExportFailed = stringResource(Str.compendium_messages_export_failed)
        val fileSaver = rememberFileSaver(
            FileType.JSON,
            "$partyName-compendium",
        ) { result ->
            result.mapCatching { file ->
                try {
                    exporting = true
                    val json = screenModel.buildExportJson()
                    file.writeBytes(json.toByteArray())
                } finally {
                    file.close()
                    exporting = false
                }
            }.onFailure {
                Napier.e(it.toString(), it)

                snackbarHolder.showSnackbar(messageExportFailed, SnackbarDuration.Long)

                exporting = false
            }
        }

        if (exporting) {
            FullScreenProgress()
            return
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(Spacing.bodyPadding),
            contentAlignment = Alignment.Center,
        ) {
            Button(onClick = { fileSaver.selectLocation() }) {
                Text(stringResource(Str.compendium_button_export).uppercase())
            }
        }
    }
}
