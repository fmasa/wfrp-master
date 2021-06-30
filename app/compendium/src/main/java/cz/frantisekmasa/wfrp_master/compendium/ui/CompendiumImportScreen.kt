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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import cz.frantisekmasa.wfrp_master.compendium.R
import cz.frantisekmasa.wfrp_master.compendium.domain.importer.RulebookCompendiumImporter
import cz.frantisekmasa.wfrp_master.core.ui.buttons.BackButton
import cz.frantisekmasa.wfrp_master.core.ui.scaffolding.Subtitle
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.LocalActivity
import cz.frantisekmasa.wfrp_master.core.viewModel.viewModel
import cz.frantisekmasa.wfrp_master.navigation.Route
import cz.frantisekmasa.wfrp_master.navigation.Routing
import kotlinx.coroutines.*
import org.koin.core.parameter.parametersOf
import timber.log.Timber

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
                Text(stringResource(R.string.title_compendium_import))
                viewModel.party.observeAsState().value?.let {
                    Subtitle(it.getName())
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
                        Timber.e(e)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                when (e) {
                                    is OutOfMemoryError -> R.string.error_import_not_enough_memory
                                    else -> R.string.error_import_failed
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
        val storeUrl = stringResource(R.string.rulebook_store_url)

        Text(
            "Import compendium from official WFRP rulebook.",
            textAlign = TextAlign.Center,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { fileChooser.launch("application/pdf") }) {
                Text(stringResource(R.string.import_rulebook).toUpperCase(Locale.current))
            }
            OutlinedButton(
                onClick = {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl)))
                },
            ) {
                Text(stringResource(R.string.button_buy).toUpperCase(Locale.current))
            }
        }

        Text(
            "The file is not saved anywhere and never leaves your device.",
            color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    }
}

