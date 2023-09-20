@file:OptIn(ExperimentalTextApi::class)

package cz.frantisekmasa.wfrp_master.common.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cz.frantisekmasa.wfrp_master.common.Str
import cz.frantisekmasa.wfrp_master.common.character.CharacterDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.combat.translateFeatures
import cz.frantisekmasa.wfrp_master.common.character.religion.blessings.CharacterBlessingDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.religion.miracles.CharacterMiracleDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.skills.CharacterSkillDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.spells.CharacterSpellDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.talents.CharacterTalentDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.traits.CharacterTraitDetailScreen
import cz.frantisekmasa.wfrp_master.common.character.trappings.TrappingDetailScreen
import cz.frantisekmasa.wfrp_master.common.combat.domain.ArmourPart
import cz.frantisekmasa.wfrp_master.common.combat.domain.EquippedWeapon
import cz.frantisekmasa.wfrp_master.common.core.domain.Characteristic
import cz.frantisekmasa.wfrp_master.common.core.domain.Stats
import cz.frantisekmasa.wfrp_master.common.core.domain.character.CharacterTab
import cz.frantisekmasa.wfrp_master.common.core.domain.identifiers.CharacterId
import cz.frantisekmasa.wfrp_master.common.core.domain.localizedName
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Blessing
import cz.frantisekmasa.wfrp_master.common.core.domain.religion.Miracle
import cz.frantisekmasa.wfrp_master.common.core.domain.skills.Skill
import cz.frantisekmasa.wfrp_master.common.core.domain.spells.Spell
import cz.frantisekmasa.wfrp_master.common.core.domain.talents.Talent
import cz.frantisekmasa.wfrp_master.common.core.domain.traits.Trait
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.TrappingType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponEquip
import cz.frantisekmasa.wfrp_master.common.core.ui.flow.collectWithLifecycle
import cz.frantisekmasa.wfrp_master.common.core.ui.navigation.LocalNavigationTransaction
import cz.frantisekmasa.wfrp_master.common.core.ui.primitives.Spacing
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Immutable
data class StatBlockData(
    val note: Flow<String>,
    val skills: Flow<List<Skill>>,
    val talents: Flow<List<Talent>>,
    val spells: Flow<List<Spell>>,
    val blessings: Flow<List<Blessing>>,
    val miracles: Flow<List<Miracle>>,
    val traits: Flow<List<Trait>>,
    val weapons: Flow<List<EquippedWeapon>>,
    val armour: Flow<List<ArmourPart>>,
)

@Composable
fun StatBlock(
    characterId: CharacterId,
    characteristics: Stats,
    data: StatBlockData,
) {
    CompactCharacteristicsTable(characteristics)

    CharacterItemList(
        title = stringResource(Str.character_title_weapons),
        items = data.weapons,
        value = {
            buildString {
                val weapon = it.weapon
                append(it.trapping.name)
                append(" (+")
                append(
                    (
                        sequenceOf(it.damage.value.toString()) +
                            sequenceOf(
                                if (weapon is TrappingType.RangedWeapon)
                                    listOf(
                                        weapon.range.calculate(
                                            strengthBonus = characteristics.strengthBonus,
                                        ).value.toString()
                                    )
                                else emptyList(),
                                if (weapon.equipped == WeaponEquip.OFF_HAND)
                                    listOf(WeaponEquip.OFF_HAND.localizedName)
                                else emptyList(),
                                translateFeatures(weapon.qualities),
                                translateFeatures(weapon.flaws),
                                translateFeatures(it.trapping.itemQualities.associateWith { 1 }),
                                translateFeatures(it.trapping.itemFlaws.associateWith { 1 }),
                            ).flatten()
                                .sorted()
                        ).joinToString(", ")
                )
                append(')')
            }
        },
        detail = { TrappingDetailScreen(characterId, it.trapping.id) },
        key = { it.trapping.id.toString() },
    )

    CharacterItemList(
        title = stringResource(Str.armour_title),
        items = data.armour,
        value = { "${it.hitLocation.localizedName} ${it.points.value}" },
        detail = {
            CharacterDetailScreen(
                characterId,
                comingFromCombat = true,
                initialTab = CharacterTab.COMBAT,
            )
        },
        key = { it.hitLocation.name },
    )

    CharacterItemList(
        title = stringResource(Str.traits_title_traits),
        items = data.traits,
        value = { it.evaluatedName },
        detail = { CharacterTraitDetailScreen(characterId, it.id) },
        key = { it.id.toString() },
    )

    CharacterItemList(
        title = stringResource(Str.skills_title_skills),
        items = remember(data.skills) {
            // Only basic skills can have 0 advances,
            // and these can be easily derived from characteristics
            data.skills.map { skills -> skills.filter { it.advances > 0 } }
        },
        value = { "${it.name} ${characteristics.get(it.characteristic) + it.advances}" },
        key = { it.id.toString() },
        detail = { CharacterSkillDetailScreen(characterId, it.id) },
    )

    CharacterItemList(
        title = stringResource(Str.talents_title_talents),
        items = data.talents,
        value = { it.name + if (it.taken > 1) " ${it.taken}" else "" },
        key = { it.id.toString() },
        detail = { CharacterTalentDetailScreen(characterId, it.id) },
    )

    CharacterItemList(
        title = stringResource(Str.spells_title_spells),
        items = data.spells,
        value = { it.name },
        key = { it.id.toString() },
        detail = { CharacterSpellDetailScreen(characterId, it.id) },
    )

    CharacterItemList(
        title = stringResource(Str.blessings_title),
        items = data.blessings,
        value = { it.name },
        key = { it.id.toString() },
        detail = { CharacterBlessingDetailScreen(characterId, it.id) },
    )

    CharacterItemList(
        title = stringResource(Str.miracles_title),
        items = data.miracles,
        value = { it.name },
        key = { it.id.toString() },
        detail = { CharacterMiracleDetailScreen(characterId, it.id) },
    )

    val note = data.note.collectWithLifecycle("").value

    if (note != "") {
        Divider(Modifier.padding(top = Spacing.small))
        Text(note)
    }
}

private const val SkillTag = "[skill]"

@Composable
private fun <T> CharacterItemList(
    title: String,
    items: Flow<List<T>>,
    key: (T) -> String,
    value: @Composable (T) -> String,
    detail: (item: T) -> Screen,
) {
    val itemList = items.collectWithLifecycle(emptyList()).value

    if (itemList.isEmpty()) {
        return
    }

    val formattedItems = itemList.map { key(it) to value(it) }
    val text = remember(formattedItems, key, value) {
        buildAnnotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append(title)
                append(": ")
            }

            formattedItems.forEachIndexed { index, (key, value) ->
                withAnnotation(SkillTag, key) {
                    append(value)
                }

                if (index != itemList.lastIndex) {
                    append(", ")
                }
            }
        }
    }

    val navigation = LocalNavigationTransaction.current
    ClickableText(
        text,
        style = MaterialTheme.typography.body2.copy(
            color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
        )
    ) { offset ->
        text.getStringAnnotations(SkillTag, offset, offset)
            .firstOrNull()
            ?.let { range ->
                navigation.navigate(detail(itemList.first { key(it) == range.item }))
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
