package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.RulebookCompendiumImporter
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.FileType
import cz.frantisekmasa.wfrp_master.common.core.shared.rememberFileChooser
import cz.frantisekmasa.wfrp_master.common.core.shared.rememberUrlOpener
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import java.lang.OutOfMemoryError

class CompendiumImportScreen(
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

        val snackbarHolder = LocalPersistentSnackbarHolder.current
        val fileChooser = rememberFileChooser { result ->
            result.mapCatching { file ->
                importState = ImportDialogState.LoadingItems

                val importer = RulebookCompendiumImporter(file.stream)

                val skills = async { importer.importSkills() }
                val talents = async { importer.importTalents() }
                val spells = async { importer.importSpells() }
                val blessings = async { importer.importBlessings() }
                val miracles = async { importer.importMiracles() }
                val traits = async { importer.importTraits() }

                importState = ImportDialogState.PickingItemsToImport(
                    skills.await(),
                    talents.await(),
                    spells.await(),
                    blessings.await(),
                    miracles.await(),
                    traits.await(),
                )
            }.onFailure {
                Napier.e(it.toString(), it)

                snackbarHolder.showSnackbar(
                    when (it) {
                        is OutOfMemoryError -> strings.messages.outOfMemory
                        else -> strings.messages.importFailed
                    },
                    SnackbarDuration.Long,
                )

                importState = null
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                strings.importPrompt,
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
