package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.PdfStructure
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TableParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TwoColumnPdfLexer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description.TrappingDescriptionParser
import cz.frantisekmasa.wfrp_master.common.core.domain.Money
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Availability
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance

class BasicTrappingsParser(
    private val document: Document,
    private val structure: PdfStructure,
    private val descriptionParser: TrappingDescriptionParser,
) {
    enum class Column { LEFT, RIGHT }

    fun parse(
        typeFactory: (String) -> TrappingType?,
        tablePage: Int,
        descriptionPages: IntRange,
        column: Column,
        additionalColumn: Int? = null,
    ): List<Trapping> {
        val columns =
            TwoColumnPdfLexer(document, structure, mergeSubsequentTokens = false)
                .getTokens(tablePage)

        val columnTokens = (if (column == Column.LEFT) columns.first else columns.second).toList()

        val tokens =
            (
                if (columnTokens.any { it is Token.Heading }) {
                    columnTokens.dropWhile { it !is Token.Heading }
                } else {
                    columnTokens
                }
            )
                .asSequence()
                .dropWhile { it !is Token.BodyCellPart }
                .takeWhile { it !is Token.BoldPart }
                .toList()

        val table =
            TableParser().parseTable(
                tokens.mapNotNull {
                    when (it) {
                        is Token.BodyCellPart -> it
                        is Token.NormalPart -> Token.BodyCellPart(it.text, y = 0f, height = Float.MAX_VALUE)
                        else -> null
                    }
                },
                columnCount = if (additionalColumn != null) 5 else 4,
            )

        val descriptionsByName = descriptionParser.parse(document, structure, descriptionPages)

        return table
            .asSequence()
            .flatMap { section ->
                section.rows.mapNotNull { fullRow ->
                    val row =
                        if (additionalColumn == null) {
                            fullRow
                        } else {
                            fullRow.take(additionalColumn) + fullRow.drop(additionalColumn + 1)
                        }
                    val encumbrance = row[2].trim()
                    val reach = row[3].trim()
                    var (name, packSize) = parseNameAndPackSize(row[0].trim())
                    var priceCell = row[1].trimEnd('+')

                    if (priceCell.endsWith("/ yard")) {
                        name += ", 1 yard"

                        priceCell = priceCell.replace("/ yard", "")
                    }

                    val price = PriceParser.parse(priceCell.trimEnd('+'))

                    if (encumbrance == "–") {
                        return@mapNotNull null // Consumer's guide contains stuff like lodging
                    }

                    Trapping(
                        id = uuid4(),
                        name = name,
                        price = if (price is PriceParser.Amount) price.money else Money.ZERO,
                        packSize = packSize,
                        encumbrance = Encumbrance(encumbrance.toDoubleOrNull() ?: 0.0),
                        availability =
                            matchEnumOrNull(
                                row[3].trim(),
                                mapOf(
                                    "N/A" to Availability.COMMON,
                                    "–" to Availability.COMMON,
                                ),
                            ) ?: error("Invalid Availability ${row[3]}"),
                        trappingType = typeFactory(additionalColumn?.let { fullRow[it] } ?: ""),
                        description =
                            buildString {
                                if (encumbrance == "Varies") {
                                    append("**Price:** Varies\n")
                                }

                                if (row[1].trim().endsWith('+')) {
                                    append("**Price noted is a minimum price**")
                                }

                                if (reach == "Varies") {
                                    append("**Reach:** Varies\n")
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
