package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers

import org.junit.Test
import kotlin.test.assertEquals

class RulesParserTest {
    @Test
    fun `correctly parses nested headings`() {
        val metadata = Token.Metadata(y = 0f, height = 0f)
        val tokens =
            listOf(
                Token.Heading1("Heading 1", metadata),
                Token.NormalPart("Text 1", metadata),
                Token.Heading2("Heading 1.1", metadata),
                Token.NormalPart("Text 1.1", metadata),
                Token.Heading3("Heading 1.1.1", metadata),
                Token.NormalPart("Text 1.1.1", metadata),
                Token.Heading2("Heading 1.2", metadata),
                Token.NormalPart("Text 1.2", metadata),
                Token.Heading2("Heading 1.3", metadata),
                Token.NormalPart("Text 1.3", metadata),
                Token.Heading3("Text 1.3.1", metadata),
                Token.NormalPart("Text 1.3.1", metadata),
                Token.Heading1("Heading 2", metadata),
                Token.NormalPart("Text 2", metadata),
            )

        assertEquals(
            listOf(
                emptyList<String>() to listOf("Heading 1", "Text 1"),
                listOf("Heading 1") to listOf("Heading 1.1", "Text 1.1"),
                listOf("Heading 1", "Heading 1.1") to listOf("Heading 1.1.1", "Text 1.1.1"),
                listOf("Heading 1") to listOf("Heading 1.2", "Text 1.2"),
                listOf("Heading 1") to listOf("Heading 1.3", "Text 1.3"),
                listOf("Heading 1", "Heading 1.3") to listOf("Text 1.3.1", "Text 1.3.1"),
                emptyList<String>() to listOf("Heading 2", "Text 2"),
            ),
            RulesParser().import(TokenStream(tokens))
                .map { it.parents to listOf(it.name, it.text.trim()) }
                .toList(),
        )
    }

    @Test
    fun `flattens entries that have only child sections`() {
        val metadata = Token.Metadata(y = 0f, height = 0f)
        val tokens =
            listOf(
                Token.Heading1("Heading 1", metadata),
                Token.Heading1("Heading 2", metadata),
                Token.NormalPart("Text 2", metadata),
                Token.Heading2("Heading 2.1", metadata),
                Token.Heading2("Heading 2.2", metadata),
                Token.Heading3("Heading 2.2.1", metadata),
                Token.NormalPart("Text 2.2.1", metadata),
            )

        assertEquals(
            listOf(
                emptyList<String>() to listOf("Heading 2", "Text 2"),
                listOf("Heading 2", "Heading 2.2") to listOf("Heading 2.2.1", "Text 2.2.1"),
            ),
            RulesParser().import(TokenStream(tokens))
                .map { it.parents to listOf(it.name, it.text.trim()) }
                .toList(),
        )
    }

    @Test
    fun `unifies casing for heading`() {
        val metadata = Token.Metadata(y = 0f, height = 0f)
        val tokens =
            listOf(
                // Words will be title cased
                Token.Heading1("headinG 1", metadata),
                Token.NormalPart("Text 1", metadata),
                Token.Heading2("Heading 1.1", metadata),
                Token.NormalPart("Text 1.1", metadata),
                // "of" and "and" are not title cased
                Token.Heading2("heading of rules and other", metadata),
                Token.NormalPart("Text 1.2", metadata),
            )

        assertEquals(
            listOf(
                emptyList<String>() to listOf("Heading 1", "Text 1"),
                listOf("Heading 1") to listOf("Heading 1.1", "Text 1.1"),
                listOf("Heading 1") to listOf("Heading of Rules and Other", "Text 1.2"),
            ),
            RulesParser().import(TokenStream(tokens))
                .map { it.parents to listOf(it.name, it.text.trim()) }
                .toList(),
        )
    }
}
