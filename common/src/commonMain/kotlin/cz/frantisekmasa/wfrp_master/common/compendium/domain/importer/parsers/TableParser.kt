package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

class TableParser {
    fun parseTable(
        tokens: List<Token>,
        columnCount: Int,
    ): List<TableSection> {
        val stream = TokenStream(tokens.filterIsInstance<Token.TableValue>())

        stream.dropUntil { it is Token.TableHeading || it is Token.TableValue }

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

                repeat(columnCount) {
                    var text = stream.consumeOneOfType<Token.BodyCellPart>().text

                    val hasAnotherLine = if (it == columnCount - 1)
                        text.endsWith(", ") // e.g. list of weapon qualities in last column
                    else text.endsWith(" ") // e.g. "10% \n Perception

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
