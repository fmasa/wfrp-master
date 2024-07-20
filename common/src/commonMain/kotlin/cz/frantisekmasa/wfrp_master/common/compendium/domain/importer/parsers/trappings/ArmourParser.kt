package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.DefaultLayoutPdfLexer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TableParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description.TrappingDescriptionParser
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourPoints
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance

class ArmourParser(
    private val document: Document,
    private val structure: PdfStructure,
    private val descriptionParser: TrappingDescriptionParser,
) {
    fun parse(
        tablePage: Int,
        descriptionPages: IntRange,
    ): List<Trapping> {
        val parser = TableParser()
        val lexer = DefaultLayoutPdfLexer(document, structure, mergeSubsequentTokens = false)
        val table =
            parser.findTables(lexer, structure, tablePage, findNames = true)
                .asSequence()
                .filter { it.name.contains("armour", ignoreCase = true) }
                .flatMap { parser.parseTable(it.tokens, columnCount = 8) }

        val descriptionsByName = descriptionParser.parse(document, structure, descriptionPages)

        return table
            .filter { it.heading != null }
            .flatMap { section ->
                val armourType =
                    matchEnumOrNull<ArmourType>(section.heading!!.replace("*", ""))
                        ?: error("Invalid armour type ${section.heading}")

                section.rows.map { row ->
                    val price = PriceParser.parse(row[1])
                    val penalty = row[4].trim()
                    val name = row[0].trim()
                    val comparableName = descriptionParser.comparableName(name)

                    val footnoteNumbers =
                        sequenceOf(section.heading, name)
                            .flatMap { parser.findFootnoteReferences(it) }
                            .toSet()

                    Trapping(
                        id = uuid4(),
                        name = name,
                        price = if (price is PriceParser.Amount) price.money else Money.ZERO,
                        packSize = 1,
                        encumbrance = Encumbrance(row[2].toDoubleOrNull() ?: 0.0),
                        availability =
                            Availability.values()
                                .first { it.name.equals(row[3], ignoreCase = true) },
                        trappingType =
                            TrappingType.Armour(
                                type = armourType,
                                locations = locations(row[5]),
                                points = ArmourPoints(row[6].toInt()),
                                qualities = parseFeatures(row[7]),
                                flaws = parseFeatures(row[7]),
                            ),
                        description =
                            buildString {
                                if (penalty != "" && penalty != "–") {
                                    append("**Penalty**: $penalty\n")
                                }

                                val footnotes = footnoteNumbers.mapNotNull { section.footnotes[it] }

                                footnotes.forEach {
                                    append(it.trim())
                                    append('\n')
                                }

                                val description =
                                    descriptionsByName.firstOrNull {
                                        comparableName.startsWith(it.first, ignoreCase = true)
                                    }?.second ?: return@buildString

                                if (isNotEmpty()) {
                                    append('\n')
                                }

                                append(description)
                            },
                        isVisibleToPlayers = true,
                    )
                }
            }.toList()
    }

    private fun locations(value: String): Set<HitLocation> {
        return value.splitToSequence(",")
            .map { it.trim() }
            .flatMap {
                if (it.equals("Legs", ignoreCase = true)) {
                    return@flatMap setOf(HitLocation.LEFT_LEG, HitLocation.RIGHT_LEG)
                }

                if (it.equals("Arms", ignoreCase = true)) {
                    return@flatMap setOf(HitLocation.LEFT_ARM, HitLocation.RIGHT_ARM)
                }

                HitLocation.values()
                    .filter { location -> location.name.equals(it, ignoreCase = true) }
            }.toSet()
    }
}
