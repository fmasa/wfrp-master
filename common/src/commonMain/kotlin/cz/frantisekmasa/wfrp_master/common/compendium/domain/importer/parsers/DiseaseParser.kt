package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease

class DiseaseParser(
    private val convertTablesToText: Boolean = false,
) {
    fun import(
        document: Document,
        structure: PdfStructure,
        pages: Sequence<Int>,
    ): List<Disease> =
        buildList {
            val lexer = TwoColumnPdfLexer(document, structure)
            val tokens =
                pages
                    .flatMap { page -> lexer.getTokens(page).toList() }
                    .flatten()
                    .toList()

            val stream =
                TokenStream(
                    if (convertTablesToText) {
                        tokens
                            .map {
                                when (it) {
                                    is Token.BodyCellPart -> Token.NormalPart(it.text)
                                    is Token.TableHeadCell -> Token.BoldPart(it.text)
                                    else -> it
                                }
                            }.toList()
                    } else {
                        (tokens).toList()
                    },
                )

            stream.dropUntil { it is Token.Heading3 }

            while (stream.peek() is Token.Heading3) {
                val name = stream.consumeOneOfType<Token.Heading3>().text.trim()
                val description =
                    stream.consumeUntil { it is Token.BoldPart && it.text.startsWith("Contraction:") }
                stream.consumeOne()
                val contraction =
                    stream.consumeUntil {
                        it is Token.BoldPart && (
                            it.text.startsWith("Incubation:") ||
                                it.text.startsWith(
                                    "Duration:",
                                )
                        )
                    }

                val incubation =
                    if (stream.peek()?.text?.startsWith("Incubation:") == true) {
                        stream.consumeOne()
                        stream.consumeOneOfType<Token.NormalPart>().text.trim()
                    } else {
                        ""
                    }
                stream.consumeOne()
                val duration = stream.consumeUntil { it is Token.BoldPart && it.text.startsWith("Symptoms:") }
                stream.consumeOne()
                val symptoms =
                    stream.consumeOneOfType<Token.NormalPart>().text
                        .replace("\n", " ")
                        .replace("  ", " ")
                        .split(",", ".")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                val permanentEffects =
                    if (stream.peek() is Token.BoldPart) {
                        stream.consumeOne()
                        stream.consumeOneOfType<Token.NormalPart>().text.trim()
                    } else {
                        ""
                    }

                stream.dropUntil { it is Token.Heading }

                add(
                    Disease(
                        id = uuid4(),
                        name = name,
                        description =
                            MarkdownBuilder.buildMarkdown(
                                description.filterIsInstance<Token.ParagraphToken>(),
                            ),
                        symptoms = symptoms,
                        incubation = incubation,
                        duration = MarkdownBuilder.buildMarkdown(duration.filterIsInstance<Token.ParagraphToken>()).trim(),
                        permanentEffects = permanentEffects,
                        contraction = MarkdownBuilder.buildMarkdown(contraction.filterIsInstance<Token.ParagraphToken>()),
                    ),
                )
            }
        }
}
