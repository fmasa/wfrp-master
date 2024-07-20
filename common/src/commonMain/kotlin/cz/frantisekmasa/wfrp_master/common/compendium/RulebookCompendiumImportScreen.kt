package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.PdfCompendiumImporter
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.ArchivesOfTheEmpire1
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.ArchivesOfTheEmpire2
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.Book
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.CoreRulebook
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.EnemyInShadowsCompanion
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.SeaOfClaws
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.UpInArms
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books.WindsOfMagic
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.loadDocument
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.pdfBoxInitializer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.BlessingSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.DiseaseSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.MiracleSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SkillSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TraitSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TrappingSource
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.FileType
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import dev.icerock.moko.resources.compose.stringResource

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
                    Text(stringResource(Str.compendium_title_import_compendium))
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
        var importState by remember { mutableStateOf<ImportDialogState?>(null) }

        importState?.let {
            val navigation = LocalNavigationTransaction.current

            ImportDialog(
                state = it,
                partyId = partyId,
                screen = this,
                onDismissRequest = { importState = null },
                onComplete = navigation::goBack,
            )
        }

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.bodyPadding),
        ) {
            Text(
                stringResource(Str.compendium_rulebook_import_prompt_title),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Text(
                stringResource(Str.compendium_rulebook_import_prompt_subtitle),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier =
                    Modifier
                        .padding(bottom = Spacing.medium)
                        .align(Alignment.CenterHorizontally),
            )

            val books =
                remember {
                    listOf(
                        CoreRulebook,
                        UpInArms,
                        WindsOfMagic,
                        ArchivesOfTheEmpire1,
                        ArchivesOfTheEmpire2,
                        EnemyInShadowsCompanion,
                        SeaOfClaws,
                    )
                }

            books.forEach { book ->
                key(book) {
                    BookRow(book, onStateChange = { importState = it })
                }
            }

            Text(
                stringResource(Str.compendium_assurance),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
            )
        }
    }

    @Composable
    private fun BookRow(
        book: Book,
        onStateChange: (ImportDialogState?) -> Unit,
    ) {
        val initializePdfBox = pdfBoxInitializer()

        val errorOutOfMemory = stringResource(Str.compendium_messages_out_of_memory)
        val errorImportFailed = stringResource(Str.compendium_messages_rulebook_import_failed)
        val fileChooser =
            ImportFileChooser(
                onStateChange = onStateChange,
                importerFactory = {
                    initializePdfBox()
                    PdfCompendiumImporter(loadDocument(it.stream), book)
                },
                errorMessageFactory = {
                    when (it) {
                        is OutOfMemoryError -> errorOutOfMemory
                        else -> errorImportFailed
                    }
                },
            )

        Card {
            Column(
                Modifier.fillMaxWidth()
                    .clickable { fileChooser.open(FileType.PDF) }
                    .padding(Spacing.small),
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
    @Stable
    private fun items(book: Book): String {
        return buildList {
            if (book is SkillSource) {
                add(stringResource(Str.compendium_title_skills))
            }

            if (book is TalentSource) {
                add(stringResource(Str.compendium_title_talents))
            }

            if (book is SpellSource) {
                add(stringResource(Str.compendium_title_spells))
            }

            if (book is CareerSource) {
                add(stringResource(Str.compendium_title_careers))
            }

            if (book is TraitSource) {
                add(stringResource(Str.compendium_title_traits))
            }

            if (book is BlessingSource) {
                add(stringResource(Str.compendium_title_blessings))
            }

            if (book is MiracleSource) {
                add(stringResource(Str.compendium_title_miracles))
            }

            if (book is TrappingSource) {
                add(stringResource(Str.compendium_title_trappings))
            }

            if (book is DiseaseSource) {
                add(stringResource(Str.compendium_title_diseases))
            }
        }.joinToString(", ")
    }
}
