package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.DefaultLayoutPdfLexer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TableParser
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.AmmunitionRangeExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup

class AmmunitionParser {
    fun parse(
        document: Document,
        structure: PdfStructure,
        tablePage: Int,
        descriptionPages: IntRange,
    ): List<Trapping> {
        val table = TableParser().parseTable(
            DefaultLayoutPdfLexer(document, structure, mergeSubsequentTokens = false)
                .getTokens(tablePage)
                .toList(),
            columnCount = 7,
        )

        return table
            .asSequence()
            .filter { it.heading != null }
            .flatMap { section ->
                val weaponGroups = matchEnumSetOrNull<RangedWeaponGroup>(section.heading!!, "and")
                    ?: error("Invalid ranged weapon groups: ${section.heading}")

                section.rows.map { row ->
                    val price = PriceParser.parse(row[1])
                    val damage = row[5].trim()
                    val (name, packSize) = parseNameAndPackSize(row[0].trim())

                    Trapping(
                        id = uuid4(),
                        name = name,
                        price = if (price is PriceParser.Amount) price.money else Money.ZERO,
                        packSize = packSize,
                        encumbrance = Encumbrance(row[2].toDoubleOrNull() ?: 0.0),
                        availability = Availability.values()
                            .first { it.name.equals(row[3], ignoreCase = true) },
                        trappingType = TrappingType.Ammunition(
                            weaponGroups = weaponGroups,
                            damage = DamageExpression(if (damage == "–") "+0" else damage),
                            range = range(row[4].trim()),
                            qualities = parseFeatures(row[6]),
                            flaws = parseFeatures(row[6]),
                        ),
                        description = "",
                        isVisibleToPlayers = true,
                    )
                }
            }.toList()
    }

    private fun range(value: String): AmmunitionRangeExpression {
        val weaponRange = AmmunitionRangeExpression.Constant.WEAPON_RANGE.value

        if (value.equals("As weapon", ignoreCase = true)) {
            return AmmunitionRangeExpression(weaponRange)
        }

        if (value.equals("Half weapon", ignoreCase = true)) {
            return AmmunitionRangeExpression("$weaponRange / 2")
        }

        return AmmunitionRangeExpression(
            buildString {
                append(weaponRange)
                append(' ')
                append(if (value[0] == '–') '-' else value[0])
                append(' ')
                append(value.drop(1))
            }
        )
    }
}
