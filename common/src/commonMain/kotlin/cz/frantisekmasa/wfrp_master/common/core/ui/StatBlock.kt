@file:OptIn(ExperimentalTextApi::class)

package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.benasher44.uuid.uuidFrom
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.BlessingDetail
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.MiracleDetail
import cz.frantisekmasa.wfrp_master.common.character.skills.dialog.SkillDetail
import cz.frantisekmasa.wfrp_master.common.character.spells.dialog.SpellDetail
import cz.frantisekmasa.wfrp_master.common.character.talents.dialog.TalentDetail
import cz.frantisekmasa.wfrp_master.common.character.traits.TraitDetail
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterItem
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelable
import cz.frantisekmasa.wfrp_master.common.core.shared.Parcelize
import cz.frantisekmasa.wfrp_master.common.core.ui.dialogs.FullScreenDialog
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource

@Immutable
@Parcelize
data class StatBlockData(
    val note: String,
    val skills: List<Skill>,
    val talents: List<Talent>,
    val spells: List<Spell>,
    val blessings: List<Blessing>,
    val miracles: List<Miracle>,
    val traits: List<Trait>,
) : Parcelable

@Composable
fun StatBlock(
    characteristics: Stats,
    data: StatBlockData?,
) {
    CompactCharacteristicsTable(characteristics)

    if (data == null) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    CharacterItemList(
        title = stringResource(Str.traits_title_traits),
        items = data.traits,
        value = { it.evaluatedName },
        detail = { trait, onDismissRequest ->
            TraitDetail(
                trait = trait,
                onDismissRequest = onDismissRequest,
            )
        },
    )

    CharacterItemList(
        title = stringResource(Str.skills_title_skills),
        items = derivedStateOf {
            // Only basic skills can have 0 advances,
            // and these can be easily derived from characteristics
            data.skills.filter { it.advances > 0 }
        }.value,
        value = { "${it.name} ${characteristics.get(it.characteristic) + it.advances}" },
        detail = { skill, onDismissRequest -> SkillDetail(skill, onDismissRequest) },
    )

    CharacterItemList(
        title = stringResource(Str.talents_title_talents),
        items = data.talents,
        value = { it.name + if (it.taken > 1) " ${it.taken}" else "" },
        detail = { talent, onDismissRequest -> TalentDetail(talent, onDismissRequest) },
    )

    CharacterItemList(
        title = stringResource(Str.spells_title_spells),
        items = data.spells,
        value = { it.name },
        detail = { spell, onDismissRequest -> SpellDetail(spell, onDismissRequest) },
    )

    CharacterItemList(
        title = stringResource(Str.blessings_title),
        items = data.blessings,
        value = { it.name },
        detail = { blessing, onDismissRequest ->
            BlessingDetail(
                blessing = blessing,
                onDismissRequest = onDismissRequest,
            )
        },
    )

    CharacterItemList(
        title = stringResource(Str.miracles_title),
        items = data.miracles,
        value = { it.name },
        detail = { miracle, onDismissRequest ->
            MiracleDetail(
                miracle = miracle,
                onDismissRequest = onDismissRequest,
            )
        },
    )

    if (data.note != "") {
        Divider(Modifier.padding(top = Spacing.small))
        Text(data.note)
    }
}

private const val SkillTag = "[skill]"

@Composable
private fun <T : CharacterItem<T, *>> CharacterItemList(
    title: String,
    items: List<T>,
    value: (T) -> String,
    detail: @Composable (item: T, onDismissRequest: () -> Unit) -> Unit
) {
    if (items.isEmpty()) {
        return
    }

    var visibleSkill: T? by rememberSaveable { mutableStateOf(null) }

    visibleSkill?.let {
        FullScreenDialog(onDismissRequest = { visibleSkill = null }) {
            detail(it) { visibleSkill = null }
        }
    }

    val text by derivedStateOf {
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(title)
                append(": ")
            }

            items.forEachIndexed { index, item ->
                withAnnotation(SkillTag, item.id.toString()) {
                    append(value(item))
                }

                if (index != items.lastIndex) {
                    append(", ")
                }
            }
        }
    }

    ClickableText(
        text,
        style = MaterialTheme.typography.body2.copy(
            color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
        )
    ) { offset ->
        text.getStringAnnotations(SkillTag, offset, offset)
            .firstOrNull()
            ?.let { range ->
                val itemId = uuidFrom(range.item)

                visibleSkill = items.first { it.id == itemId }
            }
    }
}

@Composable
private fun CompactCharacteristicsTable(characteristics: Stats) {
    Row(
        Modifier.fillMaxWidth()
            .height(IntrinsicSize.Min),
    ) {
        val lastCharacteristic = remember { Characteristic.ORDER.last() }
        Characteristic.ORDER.forEach { characteristic ->
            key(characteristic) {
                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(characteristic.shortcut),
                        modifier = Modifier.padding(vertical = Spacing.tiny)
                    )

                    Divider(color = MaterialTheme.colors.onSurface)

                    Text(
                        text = characteristics.get(characteristic).toString(),
                        modifier = Modifier.padding(vertical = Spacing.tiny)
                    )
                }
            }

            if (characteristic != lastCharacteristic) {
                Divider(
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                )
            }
        }
    }
}
