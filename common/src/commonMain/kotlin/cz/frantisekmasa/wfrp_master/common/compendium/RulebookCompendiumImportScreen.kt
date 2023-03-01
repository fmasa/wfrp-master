package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.PdfCompendiumImporter
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.Book
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.CoreRulebook
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.UpInArms
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.WindsOfMagic
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.loadDocument
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.pdfBoxInitializer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.BlessingSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.MiracleSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SkillSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TraitSource
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.FileType
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings

class RulebookCompendiumImportScreen(
    private val partyId: PartyId,
) : Screen {
    @Composable
    override fun Content() {
        Scaffold(topBar = { TopBar() }) {
            MainContainer()
        }
    }

    @Composable
    private fun TopBar() {
        val partyScreenModel: PartyScreenModel = rememberScreenModel(arg = partyId)
        TopAppBar(
            title = {
                Column {
                    Text(LocalStrings.current.compendium.titleImportCompendium)
                    partyScreenModel.party.collectWithLifecycle(null).value?.let {
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
                screen = this,
                onDismissRequest = { importState = null },
                onComplete = navigator::pop,
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(Spacing.bodyPadding)) {
            Text(
                strings.rulebookImportPrompt,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = Spacing.medium)
            )

            val books = remember { listOf(CoreRulebook, UpInArms, WindsOfMagic) }

            books.forEach { book ->
                key(book) {
                    BookRow(book, onStateChange = { importState = it })
                }
            }

            Text(
                strings.assurance,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2
            )
        }
    }

    @Composable
    private fun BookRow(book: Book, onStateChange: (ImportDialogState?) -> Unit) {
        val strings = LocalStrings.current.compendium
        val initializePdfBox = pdfBoxInitializer()

        val fileChooser = ImportFileChooser(
            onStateChange = onStateChange,
            importerFactory = {
                initializePdfBox()
                PdfCompendiumImporter(loadDocument(it.stream), book)
            },
            errorMessageFactory = {
                when (it) {
                    is OutOfMemoryError -> strings.messages.outOfMemory
                    else -> strings.messages.rulebookImportFailed
                }
            }
        )

        Card {
            Column(
                Modifier.fillMaxWidth()
                    .clickable { fileChooser.open(FileType.PDF) }
                    .padding(Spacing.small)
            ) {
                Text(book.name)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(items(book), style = MaterialTheme.typography.body2)
                }
            }
        }

        Spacer(Modifier.padding(bottom = Spacing.small))
    }

    @Composable
    private fun items(book: Book): String {
        val strings = LocalStrings.current

        return remember(book) {
            buildList {
                if (book is SkillSource) {
                    add(strings.compendium.tabSkills)
                }

                if (book is TalentSource) {
                    add(strings.compendium.tabTalents)
                }

                if (book is SpellSource) {
                    add(strings.compendium.tabSpells)
                }

                if (book is CareerSource) {
                    add(strings.compendium.tabCareers)
                }

                if (book is TraitSource) {
                    add(strings.compendium.tabTraits)
                }

                if (book is BlessingSource) {
                    add(strings.compendium.tabBlessings)
                }

                if (book is MiracleSource) {
                    add(strings.compendium.tabMiracles)
                }
            }.joinToString(", ")
        }
    }
}
