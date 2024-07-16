package cz.frantisekmasa.wfrp_master.common.compendium.journal.rules

import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.diseases.Symptom
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.compendium.journal.Journal
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.localization.Translator
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class DiseaseSymptomProvider(
    private val journal: Journal,
    private val partyRepository: PartyRepository,
    private val translatorFactory: Translator.Factory,
) {
    fun <T> addSymptoms(
        partyId: PartyId,
        flow: Flow<T>,
        symptomsExtractor: (T) -> List<String>,
    ): Flow<Pair<T, ImmutableList<Symptom>>> {
        val symptomsFlow =
            partyRepository.getLive(partyId).right()
                .map { it.settings.language }
                .distinctUntilChanged()
                .flatMapLatest {
                    journal.findByFolder(
                        partyId,
                        translatorFactory.create(it).translate(Str.journal_folder_symptoms),
                    )
                }

        return combine(flow, symptomsFlow) { item, allSymptoms ->
            val symptoms = symptomsExtractor(item)

            if (symptoms.isEmpty()) {
                return@combine item to persistentListOf()
            }

            val symptomsByName = allSymptoms.associateBy { comparableSymptomName(it.name) }
            item to
                symptoms.map {
                    Symptom(
                        it,
                        symptomsByName[comparableSymptomName(it)]?.id,
                        (JOURNAL_PATH + listOf(it)).joinToString(" ${JournalEntry.PARENT_SEPARATOR} "),
                    )
                }.toImmutableList()
        }
    }

    private fun comparableSymptomName(name: String): String {
        return name.replace(SYMPTOM_SPECIFICATION_REGEX, "")
            .trim()
            .lowercase()
    }

    companion object {
        private val SYMPTOM_SPECIFICATION_REGEX = Regex("\\([a-zA-Z ]+\\)")
        private val JOURNAL_PATH = listOf("Rules", "Disease and Infection", "Symptoms")
    }
}
