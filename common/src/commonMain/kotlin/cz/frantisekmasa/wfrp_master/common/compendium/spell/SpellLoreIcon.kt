package cz.frantisekmasa.wfrp_master.common.compendium.spell

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.core.shared.Resources
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.ItemIcon
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.defaultBackgroundColor

@Composable
fun SpellLoreIcon(lore: SpellLore?) {
    val (icon, backgroundColor) =
        when (lore) {
            SpellLore.BEASTS -> Resources.Drawable.LoreBeasts to Color(78, 52, 46)
            SpellLore.DEATH -> Resources.Drawable.LoreDeath to Color(106, 27, 154)
            SpellLore.FIRE -> Resources.Drawable.LoreFire to Color(198, 40, 40)
            SpellLore.HEAVENS -> Resources.Drawable.LoreHeavens to Color(57, 73, 171)
            SpellLore.METAL -> Resources.Drawable.LoreMetal to Color(255, 160, 0)
            SpellLore.LIFE -> Resources.Drawable.LoreLife to Color(124, 179, 66)
            SpellLore.LIGHT -> Resources.Drawable.LoreLight to Color(255, 214, 0)
            SpellLore.SHADOWS -> Resources.Drawable.LoreShadows to Color(66, 66, 66)
            SpellLore.HEDGECRAFT -> Resources.Drawable.LoreHedgecraft to Color(85, 139, 47)
            SpellLore.WITCHCRAFT -> Resources.Drawable.LoreWitchcraft to Color(69, 39, 160)
            SpellLore.DAEMONOLOGY -> Resources.Drawable.LoreDaemonology to Color(69, 39, 160)
            SpellLore.NECROMANCY -> Resources.Drawable.LoreNecromancy to Color(66, 66, 66)
            SpellLore.NURGLE -> Resources.Drawable.LoreNurgle to Color(158, 157, 36)
            SpellLore.SLAANESH -> Resources.Drawable.LoreSlaanesh to Color(173, 20, 87)
            SpellLore.TZEENTCH -> Resources.Drawable.LoreTzeentch to Color(142, 36, 170)
            SpellLore.PETTY -> Resources.Drawable.LorePettySpells to defaultBackgroundColor()
            null -> Resources.Drawable.Spell to defaultBackgroundColor()
        }

    ItemIcon(
        drawable = icon,
        backgroundColor = backgroundColor,
    )
}
