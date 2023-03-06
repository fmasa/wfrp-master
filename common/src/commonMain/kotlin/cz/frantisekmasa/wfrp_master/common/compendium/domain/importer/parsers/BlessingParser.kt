package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import com.benasher44.uuid.uuid4
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing

class BlessingParser {

    fun import(
        document: Document,
        structure: PdfStructure,
        page: Int,
    ): List<Blessing> = buildList {
        val lexer = DefaultLayoutPdfLexer(document, structure)
        val tokens = sequenceOf(page)
            .flatMap { lexer.getTokens(page).toList() }
            .toList()

        val stream = TokenStream(tokens)

        stream.dropUntil { it is Token.Heading3 }

        while (stream.peek() is Token.Heading3) {
            val name = stream.consumeOneOfType<Token.Heading3>().text

            // Range: <String>
            stream.consumeOneOfType<Token.BoldPart>()
            val range = stream.consumeOneOfType<Token.NormalPart>().text

            // Target: <String>
            stream.consumeOneOfType<Token.BoldPart>()
            val target = stream.consumeOneOfType<Token.NormalPart>().text

            // Duration: <String>
            stream.consumeOneOfType<Token.BoldPart>()
            val (duration, effectStart) = stream.consumeOneOfType<Token.NormalPart>().text
                .split('\n', limit = 2)

            val effect = MarkdownBuilder.buildMarkdown(
                listOf(Token.NormalPart(effectStart)) +
                    stream.consumeUntil { it is Token.Heading3 }
                        .filterIsInstance<Token.ParagraphToken>()
            )

            add(
                Blessing(
                    id = uuid4(),
                    name = name.trim(),
                    range = range.trim(),
                    target = target.trim(),
                    duration = duration.trim(),
                    effect = effect.trim(),
                )
            )
        }
    }
}
