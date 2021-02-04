package cz.frantisekmasa.wfrp_master.compendium.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
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
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.AmbientActivity
import cz.frantisekmasa.wfrp_master.core.ui.viewinterop.registerForActivityResult
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
    val context = AmbientContext.current
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

    val fileChooser by registerFileChooser(
        onFileChoose = {
            coroutineScope.launch(Dispatchers.Default) {
                context.contentResolver.openInputStream(it)?.use { inputStream ->
                    try {
                        importState = ImportDialogState.LoadingItems

                        val importer = RulebookCompendiumImporter(inputStream)

                        val skills = async { importer.importSkills() }
                        val talents = async { importer.importTalents() }
                        val spells = async { importer.importSpells() }

                        importState = ImportDialogState.PickingItemsToImport(
                            skills.await(),
                            talents.await(),
                            spells.await(),
                        )
                    } catch (e: Throwable) {
                        Timber.e(e)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, R.string.error_import_failed, Toast.LENGTH_LONG)
                                .show()
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
        val activity = AmbientActivity.current
        val storeUrl = stringResource(R.string.rulebook_store_url)

        Text(
            "Import compendium from official WFRP rulebook.",
            textAlign = TextAlign.Center,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { fileChooser.launch(CHOOSE_COMPENDIUM_FILE) }) {
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

@Composable
private fun registerFileChooser(onFileChoose: (Uri) -> Unit): Lazy<ActivityResultLauncher<Int?>> {
    return registerForActivityResult(FileOpenContract()) { result ->
        result.intent?.data?.let(onFileChoose)
    }
}

private data class Result(val intent: Intent?)

private class FileOpenContract : ActivityResultContract<Int?, Result>() {
    override fun createIntent(context: Context, requestCode: Int?): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result {
        Timber.d(resultCode.toString())
        return Result(intent)
    }
}

private const val CHOOSE_COMPENDIUM_FILE = 2
