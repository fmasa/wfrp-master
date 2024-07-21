package cz.frantisekmasa.wfrp_master.common.compendium

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.CompendiumImporter
import cz.frantisekmasa.wfrp_master.common.core.shared.FileChooser
import cz.frantisekmasa.wfrp_master.common.core.shared.ReadableFile
import cz.frantisekmasa.wfrp_master.common.core.shared.rememberFileChooser
import cz.frantisekmasa.wfrp_master.common.core.ui.scaffolding.LocalPersistentSnackbarHolder
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

@Composable
fun ImportFileChooser(
    onStateChange: (ImportDialogState?) -> Unit,
    importerFactory: suspend (ReadableFile) -> CompendiumImporter,
    errorMessageFactory: (Throwable) -> String,
): FileChooser {
    val snackbarHolder = LocalPersistentSnackbarHolder.current

    return rememberFileChooser { result ->
        result.mapCatching { file ->
            coroutineScope {
                onStateChange(ImportDialogState.LoadingItems)

                val importer = importerFactory(file)

                val skills = async { importer.importSkills() }
                val talents = async { importer.importTalents() }
                val spells = async { importer.importSpells() }
                val blessings = async { importer.importBlessings() }
                val miracles = async { importer.importMiracles() }
                val traits = async { importer.importTraits() }
                val careers = async { importer.importCareers() }
                val trappings = async { importer.importTrappings() }
                val diseases = async { importer.importDiseases() }
                val journalEntries = async { importer.importJournalEntries() }

                onStateChange(
                    ImportDialogState.PickingItemsToImport(
                        source = importer.source,
                        skills.await(),
                        talents.await(),
                        spells.await(),
                        blessings.await(),
                        miracles.await(),
                        traits.await(),
                        careers.await(),
                        trappings.await(),
                        diseases.await(),
                        journalEntries.await(),
                        replaceExistingByDefault = false,
                    ),
                )
            }
        }.onFailure {
            Napier.e(it.toString(), it)

            snackbarHolder.showSnackbar(errorMessageFactory(it), SnackbarDuration.Long)

            onStateChange(null)
        }
    }
}
