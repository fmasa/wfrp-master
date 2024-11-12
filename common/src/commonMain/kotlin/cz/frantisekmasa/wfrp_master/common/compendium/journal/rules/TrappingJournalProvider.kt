package cz.frantisekmasa.wfrp_master.common.compendium.journal.rules

import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.compendium.journal.Journal
import cz.frantisekmasa.wfrp_master.common.core.domain.NamedEnum
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ItemFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ItemQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponFlaw
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import cz.frantisekmasa.wfrp_master.common.localization.Translator
import dev.icerock.moko.resources.StringResource
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.enums.enumEntries

class TrappingJournalProvider(
    private val journal: Journal,
    private val partyRepository: PartyRepository,
    private val translatorFactory: Translator.Factory,
) {
    fun getTrappingJournal(partyId: PartyId): Flow<TrappingJournal> =
        partyRepository.getLive(partyId).right()
            .map { it.settings.language }
            .distinctUntilChanged()
            .flatMapLatest { language ->
                val translator = translatorFactory.create(language)

                journal.findByFolders(
                    partyId,
                    listOf(
                        translator.translate(Str.journal_folder_item_qualities),
                        translator.translate(Str.journal_folder_item_flaws),
                        translator.translate(Str.journal_folder_weapon_qualities),
                        translator.translate(Str.journal_folder_weapon_flaws),
                        translator.translate(Str.journal_folder_armour_flaws),
                        translator.translate(Str.journal_folder_armour_qualities),
                        translator.translate(Str.journal_folder_armour_flaws),
                        translator.translate(Str.journal_folder_weapon_melee_groups),
                        translator.translate(Str.journal_folder_weapon_ranged_groups),
                    ),
                ).map { entries ->
                    val journalEntries =
                        entries.associateBy {
                            (it.parents + it.name.trim().replace(RATING_REGEX, "").lowercase())
                        }

                    TrappingJournal(
                        itemQualities =
                            buildEntries(
                                partyId,
                                translator,
                                Str.journal_folder_item_qualities,
                                journalEntries,
                            ),
                        itemFlaws =
                            buildEntries(
                                partyId,
                                translator,
                                Str.journal_folder_item_flaws,
                                journalEntries,
                            ),
                        weaponQualities =
                            buildEntries(
                                partyId,
                                translator,
                                Str.journal_folder_weapon_qualities,
                                journalEntries,
                            ),
                        weaponFlaws =
                            buildEntries(
                                partyId,
                                translator,
                                Str.journal_folder_weapon_flaws,
                                journalEntries,
                            ),
                        armourQualities =
                            buildEntries(
                                partyId,
                                translator,
                                Str.journal_folder_armour_qualities,
                                journalEntries,
                            ),
                        armourFlaws =
                            buildEntries(
                                partyId,
                                translator,
                                Str.journal_folder_armour_flaws,
                                journalEntries,
                            ),
                        meleeWeaponGroups =
                            buildEntries(
                                partyId,
                                translator,
                                Str.journal_folder_weapon_melee_groups,
                                journalEntries,
                            ),
                        rangedWeaponGroups =
                            buildEntries(
                                partyId,
                                translator,
                                Str.journal_folder_weapon_ranged_groups,
                                journalEntries,
                            ),
                    )
                }
            }

    companion object {
        private val RATING_REGEX = """ \(.*\)$""".toRegex()
    }
}

@OptIn(ExperimentalStdlibApi::class)
private inline fun <reified T> buildEntries(
    partyId: PartyId,
    translator: Translator,
    folder: StringResource,
    journalEntries: Map<List<String>, JournalEntry>,
): ImmutableMap<T, TrappingJournal.Entry> where T : NamedEnum, T : Enum<T> {
    val folderPath = translator.translate(folder)
    val parents =
        folderPath
            .split(JournalEntry.PARENT_SEPARATOR)
            .map { it.trim() }

    return enumEntries<T>().associateWith {
        val name = translator.translate(it.translatableName)
        val journalEntry = journalEntries[parents + name.lowercase()]

        TrappingJournal.Entry(
            partyId = partyId,
            journalEntryId = journalEntry?.id,
            journalEntryName = "$folderPath ${JournalEntry.PARENT_SEPARATOR} $name",
        )
    }.toImmutableMap()
}

data class TrappingJournal(
    val itemQualities: ImmutableMap<ItemQuality, Entry>,
    val itemFlaws: ImmutableMap<ItemFlaw, Entry>,
    val weaponQualities: ImmutableMap<WeaponQuality, Entry>,
    val weaponFlaws: ImmutableMap<WeaponFlaw, Entry>,
    val armourQualities: ImmutableMap<ArmourQuality, Entry>,
    val armourFlaws: ImmutableMap<ArmourFlaw, Entry>,
    val meleeWeaponGroups: ImmutableMap<MeleeWeaponGroup, Entry>,
    val rangedWeaponGroups: ImmutableMap<RangedWeaponGroup, Entry>,
) {
    data class Entry(
        val partyId: PartyId,
        val journalEntryId: Uuid?,
        val journalEntryName: String,
    )
}
