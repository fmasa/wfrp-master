package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.DefaultLayoutPdfLexer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TableParser
import cz.frantisekmasa.wfrp_master.common.core.domain.HitLocation
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourPoints
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.ArmourType
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance

class ArmourParser {
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
            columnCount = 8,
        )

        return table
            .asSequence()
            .filter { it.heading != null }
            .flatMap { section ->
                val armourType = matchEnumOrNull<ArmourType>(section.heading!!.replace("*", ""))
                    ?: error("Invalid armour type ${section.heading}")

                section.rows.map { row ->
                    val price = PriceParser.parse(row[1])
                    val penalty = row[4].trim()

                    Trapping(
                        id = uuid4(),
                        name = row[0].trim(),
                        price = if (price is PriceParser.Amount) price.money else Money.ZERO,
                        packSize = 1,
                        encumbrance = Encumbrance(row[2].toDoubleOrNull() ?: 0.0),
                        availability = Availability.values()
                            .first { it.name.equals(row[3], ignoreCase = true) },
                        trappingType = TrappingType.Armour(
                            type = armourType,
                            locations = locations(row[5]),
                            points = ArmourPoints(row[6].toInt()),
                            qualities = parseFeatures(row[7]),
                            flaws = parseFeatures(row[7]),
                        ),
                        description = if (penalty != "")
                            "**Penalty**: $penalty\n"
                        else "",
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
