package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.DefaultLayoutPdfLexer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TableParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description.TrappingDescriptionParser
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.AmmunitionRangeExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup

class AmmunitionParser(
    private val document: Document,
    private val structure: PdfStructure,
    private val descriptionParser: TrappingDescriptionParser,
) {
    fun parse(
        tablePage: Int,
        descriptionPages: IntRange,
    ): List<Trapping> {
        val parser = TableParser()
        val lexer = DefaultLayoutPdfLexer(
            document,
            structure,
            mergeSubsequentTokens = false,
            sortTokens = true,
        )
        val table = parser.findNamedTables(lexer, tablePage)
            .filter { it.name.contains("ammunition", ignoreCase = true) }
            .asSequence()
            .flatMap { parser.parseTable(it.tokens, columnCount = 7) }

        val descriptionsByName = descriptionParser.parse(document, structure, descriptionPages)

        return table
            .filter { it.heading != null }
            .flatMap { section ->
                val weaponGroups = matchEnumSetOrNull<RangedWeaponGroup>(
                    section.heading!!.replace("*", ""),
                    separator = "and",
                )
                    ?: error("Invalid ranged weapon groups: ${section.heading}")

                section.rows.map { row ->
                    val price = PriceParser.parse(row[1])
                    val damage = row[5].trim().replace("–", "-")
                    val (name, packSize) = parseNameAndPackSize(row[0].trim())
                    val comparableName = descriptionParser.comparableName(name)

                    Trapping(
                        id = uuid4(),
                        name = name,
                        price = if (price is PriceParser.Amount) price.money else Money.ZERO,
                        packSize = packSize,
                        encumbrance = Encumbrance(row[2].toDoubleOrNull() ?: 0.0),
                        availability = matchEnumOrNull<Availability>(
                            row[3],
                            mapOf("Rarce" to Availability.RARE)
                        )
                            ?: error("Invalid availability ${row[3]}"),
                        trappingType = TrappingType.Ammunition(
                            weaponGroups = weaponGroups,
                            damage = DamageExpression(if (damage == "-") "+0" else damage),
                            range = range(row[4].trim()),
                            qualities = parseFeatures(row[6]),
                            flaws = parseFeatures(row[6]),
                        ),
                        description = descriptionsByName.firstOrNull {
                            comparableName.startsWith(it.first, ignoreCase = true)
                        }?.second ?: "",
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
