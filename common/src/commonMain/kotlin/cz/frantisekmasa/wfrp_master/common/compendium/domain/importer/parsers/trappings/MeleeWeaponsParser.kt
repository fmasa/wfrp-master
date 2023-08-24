package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.DefaultLayoutPdfLexer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TableParser
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Reach

class MeleeWeaponsParser {

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
                val group = matchEnumOrNull<MeleeWeaponGroup>(section.heading!!.replace("*", ""))
                    ?: error("Invalid weapon group ${section.heading}")

                section.rows.map { row ->
                    val price = PriceParser.parse(row[1])
                    val encumbrance = row[2].trim()
                    val reach = row[4].trim()

                    Trapping(
                        id = uuid4(),
                        name = row[0].trim(),
                        price = if (price is PriceParser.Amount) price.money else Money.ZERO,
                        packSize = 1,
                        encumbrance = Encumbrance(encumbrance.toDoubleOrNull() ?: 0.0),
                        availability = matchEnumOrNull(
                            row[3].trim(),
                            mapOf(
                                "N/A" to Availability.COMMON,
                                "–" to Availability.COMMON,
                            ),
                        ) ?: error("Invalid Availability ${row[3]}"),
                        trappingType = TrappingType.MeleeWeapon(
                            group = group,
                            reach = matchEnumOrNull(
                                reach,
                                mapOf(
                                    "Medium" to Reach.AVERAGE,
                                    "Varies" to Reach.AVERAGE
                                ),
                            ) ?: error("Invalid Reach ${row[4]}"),
                            damage = DamageExpression(row[5].trim().replace("*", "")),
                            qualities = parseFeatures(row[6]),
                            flaws = parseFeatures(row[6]),
                        ),
                        description = buildString {
                            if (encumbrance == "Varies") {
                                append("**Price:** Varies\n")
                            }

                            if (reach == "Varies") {
                                append("**Reach:** Varies\n")
                            }
                        },
                        isVisibleToPlayers = true,
                    )
                }
            }.toList()
    }
}
