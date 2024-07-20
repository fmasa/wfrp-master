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
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponQuality
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.WeaponRangeExpression

class RangedWeaponsParser(
    private val document: Document,
    private val structure: PdfStructure,
    private val descriptionParser: TrappingDescriptionParser,
    private val rangedWeaponGroupResolver: (String) -> RangedWeaponGroup? = { null },
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
                .filter { it.name.contains("weapons", ignoreCase = true) }
                .flatMap { parser.parseTable(it.tokens, columnCount = 7) }

        val descriptionsByName = descriptionParser.parse(document, structure, descriptionPages)

        return table
            .filter { it.heading != null }
            .flatMap { section ->
                val heading = section.heading!!.replace("*", "")
                val group =
                    rangedWeaponGroupResolver(heading)
                        ?: matchEnumOrNull<RangedWeaponGroup>(heading)
                        ?: return@flatMap emptyList()

                val defaultQualities =
                    if (
                        group == RangedWeaponGroup.BLACKPOWDER ||
                        group == RangedWeaponGroup.ENGINEERING
                    ) {
                        mapOf(
                            WeaponQuality.DAMAGING to 1,
                            WeaponQuality.BLACKPOWDER to 1,
                        )
                    } else {
                        emptyMap()
                    }

                section.rows.map { row ->
                    val name = row[0]
                    val price = PriceParser.parse(row[1])
                    val damage = row[5].trim()
                    val comparableName = descriptionParser.comparableName(name)
                    val qualitiesAndFlaws = row[6]

                    val footnoteNumbers =
                        sequenceOf(
                            section.heading,
                            name,
                            damage,
                            qualitiesAndFlaws,
                        )
                            .flatMap { parser.findFootnoteReferences(it) }
                            .toSet()

                    Trapping(
                        id = uuid4(),
                        name = name.trim { it.isWhitespace() || it == '*' },
                        price = if (price is PriceParser.Amount) price.money else Money.ZERO,
                        packSize = 1,
                        encumbrance = Encumbrance(row[2].trim().toDoubleOrNull() ?: 0.0),
                        availability =
                            matchEnumOrNull(
                                row[3].trim(),
                                mapOf(
                                    "N/A" to Availability.COMMON,
                                    "–" to Availability.COMMON,
                                ),
                            ) ?: error("Invalid Availability ${row[3]}"),
                        trappingType =
                            TrappingType.RangedWeapon(
                                group = group,
                                range =
                                    WeaponRangeExpression(
                                        row[4]
                                            .replace("x", " * ")
                                            .replace("×", " * ")
                                            .replace("  ", " ")
                                            .trim(),
                                    ),
                                damage =
                                    DamageExpression(
                                        if (damage == "–") {
                                            "0"
                                        } else {
                                            damage.trim { it.isWhitespace() || it == '*' }
                                        },
                                    ),
                                qualities =
                                    parseFeatures<WeaponQuality>(qualitiesAndFlaws) +
                                        defaultQualities,
                                flaws = parseFeatures(qualitiesAndFlaws),
                            ),
                        description =
                            buildString {
                                val footnotes = footnoteNumbers.mapNotNull { section.footnotes[it] }

                                footnotes.forEach {
                                    append(it)
                                    append('\n')
                                }

                                val description =
                                    descriptionsByName.firstOrNull {
                                        comparableName.startsWith(it.first, ignoreCase = true)
                                    }?.second ?: return@buildString

                                if (footnotes.isNotEmpty()) {
                                    append('\n')
                                }

                                append(description)
                            },
                        isVisibleToPlayers = true,
                    )
                }
            }.toList()
    }
}
