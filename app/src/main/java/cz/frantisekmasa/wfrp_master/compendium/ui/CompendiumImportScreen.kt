package cz.frantisekmasa.wfrp_master.compendium.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.common.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.common.core.ui.viewinterop.LocalActivity
import cz.frantisekmasa.wfrp_master.common.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.common.localization.LocalStrings
import cz.frantisekmasa.wfrp_master.compendium.domain.importer.RulebookCompendiumImporter
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf

@Composable
fun CompendiumImportScreen(routing: Routing<Route.CompendiumImport>) {
    Scaffold(topBar = { TopBar(routing) }) {
        MainContainer(routing)
    }
}

@Composable
private fun TopBar(routing: Routing<Route.CompendiumImport>) {
    val viewModel: CompendiumViewModel by viewModel { parametersOf(routing.route.partyId) }

    TopAppBar(
        title = {
            Column {
                Text(LocalStrings.current.compendium.titleImportCompendium)
                viewModel.party.collectWithLifecycle(null).value?.let {
                    Subtitle(it.name)
                }
            }
        },
        navigationIcon = { BackButton(onClick = { routing.pop() }) },
    )
}

@Composable
private fun MainContainer(routing: Routing<Route.CompendiumImport>) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val strings = LocalStrings.current.compendium

    var importState by remember { mutableStateOf<ImportDialogState?>(null) }

    importState?.let {
        ImportDialog(
            state = it,
            partyId = routing.route.partyId,
            onDismissRequest = { importState = null },
            onComplete = { routing.pop() },
        )
    }

    val fileChooser = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
        onResult = {
            coroutineScope.launch(Dispatchers.IO) {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    try {
                        importState = ImportDialogState.LoadingItems

                        val importer = RulebookCompendiumImporter(inputStream)

                        val skills = async { importer.importSkills() }
                        val talents = async { importer.importTalents() }
                        val spells = async { importer.importSpells() }
                        val blessings = async { importer.importBlessings() }
                        val miracles = async { importer.importMiracles() }

                        importState = ImportDialogState.PickingItemsToImport(
                            skills.await(),
                            talents.await(),
                            spells.await(),
                            blessings.await(),
                            miracles.await(),
                        )
                    } catch (e: Throwable) {
                        Napier.e(e.toString(), e)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                when (e) {
                                    is OutOfMemoryError -> strings.messages.outOfMemory
                                    else -> strings.messages.importFailed
                                },
                                Toast.LENGTH_LONG
                            ).show()
                            importState = null
                        }
                    }
                }

                Firebase.analytics.logEvent("compendium_imported", null)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val activity = LocalActivity.current

        Text(
            strings.importPrompt,
            textAlign = TextAlign.Center,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { fileChooser.launch("application/pdf") }) {
                Text(strings.buttonImportRulebook.uppercase())
            }
            OutlinedButton(
                onClick = {
                    activity.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(strings.rulebookStoreLink))
                    )
                },
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
