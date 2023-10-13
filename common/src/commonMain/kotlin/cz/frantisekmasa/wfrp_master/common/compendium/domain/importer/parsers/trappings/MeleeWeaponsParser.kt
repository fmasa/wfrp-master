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
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.DamageExpression
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Reach

class MeleeWeaponsParser(
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
        val table = parser.findTables(lexer, structure, tablePage, findNames = false)
            .asSequence()
            .flatMap { parser.parseTable(it.tokens, columnCount = 7) }

        val descriptionsByName = descriptionParser.parse(document, structure, descriptionPages)

        return table
            .filter { it.heading != null }
            .flatMap { section ->
                val group = matchEnumOrNull<MeleeWeaponGroup>(
                    section.heading!!.replace("*", ""),
                    mapOf(
                        "PARRYING" to MeleeWeaponGroup.PARRY,
                    )
                )
                    ?: error("Invalid weapon group ${section.heading}")

                section.rows.map { row ->
                    val name = row[0].trim()
                    val price = PriceParser.parse(row[1])
                    val encumbrance = row[2].trim()
                    val reach = row[4].trim()
                    val damage = row[5].trim()
                    val qualitiesAndFlaws = row[6]

                    val footnoteNumbers = sequenceOf(damage, qualitiesAndFlaws)
                        .flatMap { parser.findFootnoteReferences(it) }

                    Trapping(
                        id = uuid4(),
                        name = name,
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
                                    "N/A" to Reach.AVERAGE,
                                    "Medium" to Reach.AVERAGE,
                                    "Varies" to Reach.AVERAGE
                                ),
                            ) ?: error("Invalid Reach ${row[4]}"),
                            damage = DamageExpression(damage.replace("*", "")),
                            qualities = parseFeatures(qualitiesAndFlaws),
                            flaws = parseFeatures(qualitiesAndFlaws),
                        ),
                        description = buildString {
                            if (encumbrance == "Varies") {
                                append("**Price:** Varies\n")
                            }

                            if (reach == "Varies") {
                                append("**Reach:** Varies\n")
                            }

                            val footnotes = footnoteNumbers.mapNotNull { section.footnotes[it] }

                            footnotes.forEach {
                                append(it)
                                append('\n')
                            }

                            val comparableName = descriptionParser.comparableName(name)
                            descriptionsByName.firstOrNull {
                                comparableName.startsWith(it.first, ignoreCase = true)
                            }?.let {
                                if (isNotEmpty()) {
                                    append("\n")
                                }

                                append(it.second)
                            }
                        },
                        isVisibleToPlayers = true,
                    )
                }
            }.toList()
    }
}
