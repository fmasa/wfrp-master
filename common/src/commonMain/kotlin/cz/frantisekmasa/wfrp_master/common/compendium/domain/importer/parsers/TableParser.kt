package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

class TableParser {
    fun findTables(
        lexer: DefaultLayoutPdfLexer,
        structure: PdfStructure,
        tablePage: Int,
        findNames: Boolean,
    ): List<Table> {
        val tokens =
            lexer.getTokens(tablePage)
                .toList()

        val tables = mutableListOf<Table>()

        val stream = TokenStream(tokens)

        while (stream.peek() != null) {
            val tableName =
                if (findNames) {
                    stream.dropUntil { it is Token.BoxHeader || it is Token.TableValue }
                    stream.consumeWhileOfType<Token.BoxHeader>().joinToString("") { it.text }
                } else {
                    ""
                }

            if (stream.peek() is Token.BoxContent) {
                continue
            }

            stream.dropUntil { it is Token.TableValue }
            stream.dropWhile { it is Token.TableHeadCell }

            tables +=
                Table(
                    name = tableName,
                    tokens =
                        stream.consumeWhile {
                            it is Token.TableHeading ||
                                it is Token.BodyCellPart || it is Token.ItalicsPart ||
                                (it is Token.NormalPart && structure.tableFootnotesAsNormalText)
                        },
                )
        }

        return tables
    }

    data class Table(
        val name: String,
        val tokens: List<Token>,
    )

    fun findFootnoteReferences(text: String): Set<Int> {
        return FOOTNOTE_REFERENCE.findAll(text)
            .map { it.groupValues[1].length }
            .toSet()
    }

    private fun isFootnoteStart(token: Token?): Boolean {
        return (
            token is Token.NormalPart ||
                token is Token.BodyCellPart ||
                token is Token.ItalicsPart
        ) && token.text.startsWith("*")
    }

    private fun parseFootnotes(stream: TokenStream): Map<Int, String> {
        return buildMap {
            while (isFootnoteStart(stream.peek())) {
                val footnoteTokens = stream.consumeWhile { '.' !in it.text } + stream.consumeOne()
                val footnoteNumber =
                    footnoteTokens.sumOf { token -> token.text.count { it == '*' } }

                put(
                    footnoteNumber,
                    MarkdownBuilder.buildMarkdown(
                        footnoteTokens
                            .asSequence()
                            .map {
                                when (it) {
                                    is Token.BodyCellPart -> Token.NormalPart(it.text)
                                    else -> it
                                }
                            }
                            .map {
                                when (it) {
                                    is Token.NormalPart -> Token.NormalPart(it.text.replace("*", ""))
                                    else -> it
                                }
                            }
                            .filterIsInstance<Token.ParagraphToken>()
                            .toList(),
                    ),
                )
            }
        }
    }

    fun parseTable(
        tokens: List<Token>,
        columnCount: Int,
    ): List<TableSection> {
        val stream =
            TokenStream(
                tokens.filter { it is Token.TableValue || it is Token.NormalPart || it is Token.ItalicsPart },
            )

        stream.dropUntil { it is Token.TableHeading || it is Token.BodyCellPart }

        val tableSections =
            buildList {
                var heading: String? = null
                val rows = mutableListOf<List<String>>()
                var footnotes = mapOf<Int, String>()

                while (stream.peek() != null) {
                    if (isFootnoteStart(stream.peek())) {
                        footnotes = parseFootnotes(stream)
                        continue
                    }

                    if (stream.peek() is Token.TableHeading) {
                        if (rows.isNotEmpty()) {
                            add(TableSection(heading, rows.toList(), footnotes))
                            rows.clear()
                            footnotes = emptyMap()
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

                        try {
                            lastToken = stream.consumeOneOfType<Token.BodyCellPart>()
                        } catch (e: Throwable) {
                            throw e
                        }
                        var text = lastToken.text

                        val hasAnotherLine =
                            if (isLastColumn) {
                                (text.endsWith(", ") || text.endsWith(" or ")) // e.g. list of weapon qualities in last column
                            } else {
                                (text.endsWith(" ") || text.endsWith("’s")) // e.g. "10% \n Perception
                            }

                        if (hasAnotherLine) {
                            text += stream.consumeOneOfType<Token.BodyCellPart>().text
                        }

                        cells += text
                    }

                    if (stream.peek() is Token.NormalPart && !isFootnoteStart(stream.peek())) {
                        stream.dropUntil { it is Token.TableHeading || it is Token.BodyCellPart }
                    }

                    rows += cells
                }

                if (rows.isNotEmpty()) {
                    add(TableSection(heading, rows.toList(), footnotes))
                }
            }

        if (tableSections.isEmpty()) {
            return emptyList()
        }

        return tableSections.map {
            it.copy(
                // Sometimes footnotes are defined for all sections at the end of the table
                // this makes sure we can use them.
                footnotes = tableSections.last().footnotes + it.footnotes,
            )
        }
    }

    data class TableSection(
        val heading: String?,
        val rows: List<List<String>>,
        val footnotes: Map<Int, String>,
    )

    companion object {
        val FOOTNOTE_REFERENCE = Regex("([*]+)")
    }
}
