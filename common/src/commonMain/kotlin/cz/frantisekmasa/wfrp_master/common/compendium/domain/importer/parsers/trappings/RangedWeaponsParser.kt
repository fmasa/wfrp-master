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
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponRangeExpression

class RangedWeaponsParser {

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
                val group = matchEnumOrNull<RangedWeaponGroup>(section.heading!!.replace("*", ""))
                    ?: error("Invalid weapon group ${section.heading}")

                val defaultQualities = if (
                    group == RangedWeaponGroup.BLACKPOWDER ||
                    group == RangedWeaponGroup.ENGINEERING
                )
                    mapOf(
                        WeaponQuality.DAMAGING to 1,
                        WeaponQuality.BLACKPOWDER to 1,
                    )
                else emptyMap()

                section.rows.map { row ->
                    val price = PriceParser.parse(row[1])
                    val damage = row[5].trim().replace("*", "")

                    Trapping(
                        id = uuid4(),
                        name = row[0].replace("*", "").trim(),
                        price = if (price is PriceParser.Amount) price.money else Money.ZERO,
                        packSize = 1,
                        encumbrance = Encumbrance(row[2].trim().toDoubleOrNull() ?: 0.0),
                        availability = matchEnumOrNull(
                            row[3].trim(),
                            mapOf(
                                "N/A" to Availability.COMMON,
                                "–" to Availability.COMMON,
                            ),
                        ) ?: error("Invalid Availability ${row[3]}"),
                        trappingType = TrappingType.RangedWeapon(
                            group = group,
                            range = WeaponRangeExpression(
                                row[4]
                                    .replace("x", " * ")
                                    .replace("×", " * ")
                                    .replace("  ", " ")
                                    .trim()
                            ),
                            damage = DamageExpression(if (damage == "–") "0" else damage),
                            qualities = parseFeatures<WeaponQuality>(row[6]) + defaultQualities,
                            flaws = parseFeatures(row[6]),
                        ),
                        description = "",
                        isVisibleToPlayers = true,
                    )
                }
            }.toList()
    }
}
