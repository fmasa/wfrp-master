package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

class TableParser {
    fun parseTable(
        tokens: List<Token>,
        columnCount: Int,
    ): List<TableSection> {
        val stream = TokenStream(
            tokens.filterIsInstance<Token.TableValue>()
        )

        stream.dropUntil { it is Token.TableHeading || it is Token.BodyCellPart }

        return buildList {
            var heading: String? = null
            val rows = mutableListOf<List<String>>()

            while (stream.peek() != null) {
                if (stream.peek() is Token.TableHeading) {
                    if (rows.isNotEmpty()) {
                        add(TableSection(heading, rows.toList()))
                        rows.clear()
                    }

                    heading = stream.consumeOneOfType<Token.TableHeading>().text
                }

                val cells = mutableListOf<String>()
                lateinit var lastToken: Token.BodyCellPart

                for (column in 0 until columnCount) {
                    val nextToken = stream.peek()
                    val isLastColumn = column == columnCount - 1

                    // Handle empty last column
                    if (
                        isLastColumn &&
                        nextToken is Token.BodyCellPart &&
                        nextToken.y > lastToken.y + lastToken.height
                    ) {
                        cells += ""
                        break
                    }

                    lastToken = stream.consumeOneOfType<Token.BodyCellPart>()
                    var text = lastToken.text

                    val hasAnotherLine = if (isLastColumn)
                        (text.endsWith(", ") || text.endsWith(" or ")) // e.g. list of weapon qualities in last column
                    else (text.endsWith(" ") || text.endsWith("’s")) // e.g. "10% \n Perception

                    if (hasAnotherLine) {
                        text += stream.consumeOneOfType<Token.BodyCellPart>().text
                    }

                    cells += text
                }

                rows += cells
            }

            if (rows.isNotEmpty()) {
                add(TableSection(heading, rows.toList()))
            }
        }
    }

    data class TableSection(
        val heading: String?,
        val rows: List<List<String>>,
    )
}
