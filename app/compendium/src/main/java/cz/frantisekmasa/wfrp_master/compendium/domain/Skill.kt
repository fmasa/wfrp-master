package cz.frantisekmasa.wfrp_master.compendium.domain

import android.os.Parcelable
import cz.frantisekmasa.wfrp_master.core.common.requireMaxLength
import cz.frantisekmasa.wfrp_master.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.core.domain.compendium.CompendiumItem
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Skill(
    override val id: UUID,
    override val name: String,
    val description: String,
    val characteristic: Characteristic,
    val advanced: Boolean,
) : CompendiumItem<Skill>(), Parcelable {
    companion object {
        const val NAME_MAX_LENGTH = 50
        const val DESCRIPTION_MAX_LENGTH = 2000
    }

    init {
        require(name.isNotEmpty())
        name.requireMaxLength(NAME_MAX_LENGTH, "name")
        description.requireMaxLength(DESCRIPTION_MAX_LENGTH, "description")
    }

    override fun duplicate() = copy(id = UUID.randomUUID(), name = duplicateName())
}
