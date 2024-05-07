package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.JsonCompendiumImporter
import cz.frantisekmasa.wfrp_master.common.core.PartyScreenModel
import cz.frantisekmasa.wfrp_master.common.core.domain.ExceptionWithUserMessage
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.shared.FileType
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.rememberScreenModel
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import dev.icerock.moko.resources.compose.stringResource
import java.lang.OutOfMemoryError

class JsonCompendiumImportScreen(
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

        val errorJsonImportFailed = stringResource(Str.compendium_messages_json_import_failed)
        val errorOutOfMemory = stringResource(Str.compendium_messages_out_of_memory)
        val fileChooser =
            ImportFileChooser(
                onStateChange = { importState = it },
                importerFactory = { JsonCompendiumImporter(it.stream) },
                errorMessageFactory = {
                    when (it) {
                        is ExceptionWithUserMessage -> it.message ?: errorJsonImportFailed
                        is OutOfMemoryError -> errorOutOfMemory
                        else -> errorJsonImportFailed
                    }
                },
            )

        Column(
            modifier = Modifier.fillMaxSize().padding(Spacing.bodyPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                buildAnnotatedString {
                    append(stringResource(Str.compendium_json_import_prompt_title))
                    append('\n')
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(stringResource(Str.compendium_json_import_prompt_warning))
                    }
                    append('\n')
                    append(stringResource(Str.compendium_json_import_prompt_warning_explanation))
                },
                textAlign = TextAlign.Center,
            )

            Button(onClick = { fileChooser.open(FileType.JSON) }) {
                Text(stringResource(Str.compendium_button_import).uppercase())
            }

            Text(
                stringResource(Str.compendium_assurance),
                color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
            )
        }
    }
}
