package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent

class TalentParser {

    fun import(
        document: Document,
        structure: PdfStructure,
        pages: Sequence<Int>,
    ): List<Talent> = buildList {
        val lexer = TwoColumnPdfLexer(document, structure)
        val tokens = pages
            .flatMap { page -> lexer.getTokens(page).toList() }
            .flatten()
            .toList()

        val stream = TokenStream(tokens)

        stream.dropWhile { it !is Token.Heading3 }


        while (stream.peek() != null) {
            val name = stream.consumeOneOfType<Token.Heading3>().text.trim()

            stream.consumeOneOfType<Token.BoldPart>()

            val description = mutableListOf<Token.ParagraphToken>()

            val (maxLine, descriptionStart) = buildString {
                append(stream.consumeOneOfType<Token.NormalPart>().text)
                append('\n')
            }.split('\n', limit = 2)

            description += Token.NormalPart(descriptionStart)

            var tests = ""

            if (descriptionStart.isBlank() && stream.peek() is Token.BoldPart) {
                stream.consumeOneOfType<Token.BoldPart>()
                var testsLineAndDescription = stream.consumeOneOfType<Token.NormalPart>().text

                // Talent may affect Conditions that are written in italics
                while (testsLineAndDescription.count { it == '\n' } < 2) {
                    testsLineAndDescription += stream.consumeOneOfType<Token.ParagraphToken>().text
                }

                val (testsLine, descriptionStart2) = splitTestsFromDescription(
                    testsLineAndDescription
                )

                tests = testsLine
                description += Token.NormalPart(descriptionStart2)
            }

            description += stream.consumeUntil {
                it is Token.Heading3 || it is Token.OptionsBoxHeading
            }.filterIsInstance<Token.ParagraphToken>()

            // Drop the Options box tokens if necessary
            stream.dropUntil { it is Token.Heading3 }

            add(
                Talent(
                    id = uuid4(),
                    name = name,
                    tests = tests,
                    maxTimesTaken = maxLine.trim(),
                    description = MarkdownBuilder.buildMarkdown(description),
                )
            )
        }
    }

    private fun splitTestsFromDescription(text: String): Pair<String, String> {
        val lines = text.lines()

        val testLinesCount = if (lines[1].length < 50) 2 else 1

        val tests = lines.slice(0 until testLinesCount).joinToString(" ")

        return Pair(
            tests.replace("  ", " ").trim(),
            lines
                .asSequence()
                .drop(testLinesCount)
                .joinToString("\n")
        )
    }
}