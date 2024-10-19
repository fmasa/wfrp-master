package cz.frantisekmasa.wfrp_master.common.compendium.journal

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumItemScreenModel
import cz.frantisekmasa.wfrp_master.common.compendium.CompendiumListItem
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.core.auth.UserProvider
import cz.frantisekmasa.wfrp_master.common.core.domain.compendium.Compendium
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyId
import cz.frantisekmasa.wfrp_master.common.core.domain.party.PartyRepository
import cz.frantisekmasa.wfrp_master.common.core.utils.right
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class JournalScreenModel(
    private val partyId: PartyId,
    compendium: Compendium<JournalEntry>,
    private val firestore: FirebaseFirestore,
    userProvider: UserProvider,
    parties: PartyRepository,
) : CompendiumItemScreenModel<JournalEntry>(
        partyId,
        compendium,
    ) {
    val isGameMaster =
        parties.getLive(partyId)
            .right()
            .map { userProvider.userId == it.gameMasterId }
            .distinctUntilChanged()
    private val visibleItems =
        items.combine(isGameMaster) { items, isGameMaster ->
            if (isGameMaster) {
                items
            } else {
                items.filter { it.isVisibleToPlayers }
            }
        }

    val entries =
        visibleItems
            .map { entries ->
                entries.map(JournalEntryItem::fromEntry)
            }
            .distinctUntilChanged()

    data class JournalEntryItem(
        override val id: Uuid,
        override val name: String,
        val isVisibleToPlayers: Boolean,
    ) : CompendiumListItem {
        companion object {
            fun fromEntry(entry: JournalEntry): JournalEntryItem {
                return JournalEntryItem(
                    entry.id,
                    entry.name,
                    entry.isVisibleToPlayers,
                )
            }
        }
    }

    @Immutable
    sealed interface TreeItem {
        val key: Any?
        val name: String
        val count: Int

        @Immutable
        data class Item(
            val entry: JournalEntryItem,
            val isPinned: Boolean,
        ) : TreeItem {
            override val key: Any get() = Pair(entry.id, isPinned)
            override val name: String get() = entry.name
            override val count: Int get() = 1
        }

        @Immutable
        data class Folder(
            override val name: String,
            val items: List<TreeItem>,
        ) : TreeItem {
            override val key: String = name
            override val count: Int get() = items.sumOf { it.count }
        }
    }

    val entriesTree: Flow<List<TreeItem>> =
        visibleItems.map { items ->
            val pinned =
                items.filter { it.isPinned }
                    .map {
                        TreeItem.Item(
                            JournalEntryItem.fromEntry(it),
                            isPinned = true,
                        )
                    }

            pinned + buildTree(items)
        }

    private fun buildTree(items: List<JournalEntry>): List<TreeItem> {
        val folders = mutableMapOf<String, MutableList<JournalEntry>>()
        val entries = mutableListOf<TreeItem>()

        for (item in items) {
            if (item.parents.isEmpty()) {
                entries += TreeItem.Item(JournalEntryItem.fromEntry(item), isPinned = false)
            } else {
                folders.getOrPut(item.parents[0]) { mutableListOf() }
                    .add(item.copy(parents = item.parents.drop(1)))
            }
        }

        entries +=
            folders
                .asSequence()
                .map { (folder, subItems) -> TreeItem.Folder(folder, buildTree(subItems)) }

        return entries.sortedBy { it.name }
    }

    suspend fun save(entry: JournalEntry) {
        firestore.runTransaction {
            compendium.save(this, partyId, entry)
        }
    }

    suspend fun duplicate(entryId: Uuid) {
        firestore.runTransaction {
            compendium.save(
                this,
                partyId,
                compendium.getItem(partyId, entryId).duplicate(),
            )
        }
    }

    override suspend fun remove(compendiumItem: JournalEntry) {
        remove(compendiumItem.id)
    }

    override suspend fun update(compendiumItem: JournalEntry) {
        firestore.runTransaction {
            compendium.save(this, partyId, compendiumItem)
        }
    }

    suspend fun remove(entryId: Uuid) {
        val entry = compendium.findItem(partyId, entryId) ?: return

        firestore.runTransaction {
            compendium.remove(this, partyId, entry)
        }
    }
}
