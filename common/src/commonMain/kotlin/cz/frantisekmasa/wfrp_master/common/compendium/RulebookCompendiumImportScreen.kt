package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.lowagie.text.pdf.PdfReader
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.RulebookCompendiumImporter
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.FileType
import cz.frantisekmasa.wfrp_master.common.core.shared.rememberUrlOpener
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import java.lang.OutOfMemoryError

class RulebookCompendiumImportScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        val screenModel: CompendiumScreenModel = rememberScreenModel(arg = partyId)
        Scaffold(topBar = { TopBar(screenModel) }) {
            MainContainer()
        }
    }

    @Composable
    private fun TopBar(screenModel: CompendiumScreenModel) {
        TopAppBar(
            title = {
                Column {
                    Text(LocalStrings.current.compendium.titleImportCompendium)
                    screenModel.party.collectWithLifecycle(null).value?.let {
                        Subtitle(it.name)
                    }
                }
            },
            navigationIcon = { BackButton() },
        )
    }

    @Composable
    private fun MainContainer() {
        val strings = LocalStrings.current.compendium

        var importState by remember { mutableStateOf<ImportDialogState?>(null) }

        importState?.let {
            val navigator = LocalNavigator.currentOrThrow

            ImportDialog(
                state = it,
                partyId = partyId,
                onDismissRequest = { importState = null },
                onComplete = navigator::pop,
                screenModel = rememberScreenModel(arg = partyId)
            )
        }

        val fileChooser = ImportFileChooser(
            onStateChange = { importState = it },
            importerFactory = { RulebookCompendiumImporter(PdfReader(it.stream)) },
            errorMessageFactory = {
                when (it) {
                    is OutOfMemoryError -> strings.messages.outOfMemory
                    else -> strings.messages.rulebookImportFailed
                }
            }
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(Spacing.bodyPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                strings.rulebookImportPrompt,
                textAlign = TextAlign.Center,
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { fileChooser.open(FileType.PDF) }) {
                    Text(strings.buttonImportRulebook.uppercase())
                }

                val urlOpener = rememberUrlOpener()

                OutlinedButton(
                    onClick = {
                        urlOpener.open(strings.rulebookStoreLink, isGooglePlayLink = false)
                    }
                ) {
                    Text(strings.buttonBuy.uppercase())
                }
            }

            Text(
                strings.assurance,
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }
    }
}
