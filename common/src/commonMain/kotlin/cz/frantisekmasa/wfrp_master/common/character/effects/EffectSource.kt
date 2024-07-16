package cz.frantisekmasa.wfrp_master.common.character.effects

import androidx.compose.runtime.Stable
import com.benasher44.uuid.Uuid
import cz.frantisekmasa.wfrp_master.common.localization.Translator

interface EffectSource {
    val id: Uuid

    @Stable
    fun getEffects(translator: Translator): List<CharacterEffect> = emptyList()
}
