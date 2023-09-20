package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class JournalEntry(
    @Contextual override val id: Uuid,
    override val name: String,
    val text: String,
    val gmText: String,
    @SerialName("pinned")
    val isPinned: Boolean,
    val parents: List<String>,
    override val isVisibleToPlayers: Boolean = false,
) : CompendiumItem<JournalEntry>() {

    init {
        require(name.isNotBlank()) { "Name cannot be blank" }
        require(name.trim() == name) { "Name cannot have leading or trailing whitespaces" }
        name.requireMaxLength(NAME_MAX_LENGTH, "Name")

        text.requireMaxLength(TEXT_MAX_LENGTH, "Text")
        gmText.requireMaxLength(TEXT_MAX_LENGTH, "GM Text")
    }

    override fun replace(original: JournalEntry) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName())

    override fun changeVisibility(isVisibleToPlayers: Boolean) =
        copy(isVisibleToPlayers = isVisibleToPlayers)

    companion object {
        const val NAME_MAX_LENGTH = 50
        const val TEXT_MAX_LENGTH = 5000
        const val PARENT_SEPARATOR = '/'
    }
}
