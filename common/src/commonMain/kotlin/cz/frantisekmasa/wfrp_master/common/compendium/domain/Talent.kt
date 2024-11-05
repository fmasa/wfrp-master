package cz.frantisekmasa.wfrp_master.common.compendium.domain

import androidx.compose.runtime.Immutable
import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.common.core.serialization.UuidAsString
import cz.frantisekmasa.wfrp_master.common.core.utils.duplicateName
import dev.icerock.moko.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class Talent(
    override val id: UuidAsString,
    override val name: String,
    // TODO: Remove default value in 3.0
    val tests: String = "",
    val maxTimesTaken: String,
    val description: String,
    override val isVisibleToPlayers: Boolean = true,
) : CompendiumItem<Talent>() {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val MAX_TIMES_TAKEN_MAX_LENGTH = 100
        const val DESCRIPTION_MAX_LENGTH = 1500
        const val TESTS_MAX_LENGTH = 200
    }

    init {
        require(name.isNotEmpty())
        require(name.length <= NAME_MAX_LENGTH) { "Maximum allowed name length is $NAME_MAX_LENGTH" }
        tests.requireMaxLength(TESTS_MAX_LENGTH, "tests")
        require(description.length <= DESCRIPTION_MAX_LENGTH) { "Maximum allowed description length is $DESCRIPTION_MAX_LENGTH" }
        require(maxTimesTaken.length <= MAX_TIMES_TAKEN_MAX_LENGTH) { "Maximum length of is $MAX_TIMES_TAKEN_MAX_LENGTH" }
    }

    override fun replace(original: Talent) = copy(id = original.id)

    override fun duplicate() = copy(id = uuid4(), name = duplicateName(name, NAME_MAX_LENGTH))

    override fun changeVisibility(isVisibleToPlayers: Boolean) = copy(isVisibleToPlayers = isVisibleToPlayers)
}
