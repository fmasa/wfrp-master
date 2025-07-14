package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.JournalEntry
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Lexer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.RulesParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextPosition
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TokenStream
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TwoColumnPdfLexer
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.ArmourParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description.HeadingDescriptionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.JournalEntrySource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TrappingSource
import kotlin.math.max
import kotlin.math.min

object ArchivesOfTheEmpire3 : Book, JournalEntrySource, TrappingSource, CareerSource {
    override val name = "Archives of the Empire — Volume III"
    override val tableFootnotesAsNormalText: Boolean = true

    override fun importCareers(document: Document): List<Career> {
        return emptyList()
    }

    override fun importJournalEntries(document: Document): List<JournalEntry> {
        val lexer = TwoColumnPdfLexer(document, this)
        return buildList {
            fun parseRules(
                pages: Iterable<Int>,
                excludedTables: Set<String> = emptySet(),
                excludedBoxes: Set<String> = emptySet(),
                commonParents: List<String>,
                supportedParents: List<List<String>>,
                tokenMapper: (Token) -> Token = { it },
            ): List<JournalEntry> {
                val entries =
                    RulesParser(
                        excludedBoxes = excludedBoxes,
                        excludedTables = excludedTables,
                    ).import(
                        TokenStream(
                            pages.asSequence()
                                .flatMap { lexer.getTokens(it).toList() }
                                .flatten()
                                .map(tokenMapper),
                        ),
                    )

                return entries.filter { it.parents in supportedParents }
                    .map { it.copy(parents = commonParents + it.parents) }
                    .toList()
            }

            addAll(
                parseRules(
                   36..36,
                    commonParents = listOf("The Consumers’ Guide"),
                    supportedParents =
                    listOf(
                        listOf("Armour Rules", "Armour Qualities"),
                        listOf("Armour Rules", "Armour Flaws"),
                    ),
                )
                    .asSequence()
                    .map {
                        it.copy(
                            parents = listOf("The Consumers’ Guide", "Armour", it.parents.last()),
                        )
                    }
            )
        }
    }

    override fun importTrappings(document: Document): List<Trapping> {
        return ArmourParser(
            document,
            this,
            HeadingDescriptionParser(),
            lexerModifier = { lexer ->
                            object : Lexer {
                                override fun getTokens(page: Int): Sequence<Token> {
                                    return lexer.getTokens(page).map {
                                        when (it) {
                                            is Token.BoldPart -> Token.TableHeadCell(it.text, it.metadata)
                                            is Token.NormalPart -> Token.BodyCellPart(
                                                text = it.text,
                                                metadata = it.metadata,
                                            )
                                            is Token.Heading2 -> Token.BoxHeader(it.text, it.metadata)
                                            else -> it
                                        }
                                    }
                                }
                            }
            },
        ).parse(37, 38..38)
    }

    override fun areSameStyle(a: TextPosition, b: TextPosition): Boolean {
        return super.areSameStyle(a, b) || arePartsOfHeading2(a, b)
    }

    private fun arePartsOfHeading2(
        a: TextPosition,
        b: TextPosition,
    ): Boolean {
        // Some headings are a mix of 12pt and 18pt font
        return a.getFont().getName() == b.getFont().getName() &&
            a.getFont().getName().endsWith("CaslonAntique-Bold-SC700") &&
            min(a.getFontSizeInPt(), b.getFontSizeInPt()) == 12f &&
            max(a.getFontSizeInPt(), b.getFontSizeInPt()) == 18f
    }

    override fun resolveToken(textToken: TextToken): Token? {
        if (textToken.fontName.endsWith("CaslonAntique-Bold-SC700")) {
            if (textToken.fontSizePt == 12f || textToken.fontSizePt == 18f) {
                return Token.Heading2(textToken)
            }

            return Token.BoxHeader(textToken)
        }

        if (textToken.fontName.endsWith("CaslonAntique")) {
            if (textToken.fontSizePt == 18f) {
                return Token.BoxHeader(textToken)
            }
        }

        if (textToken.fontName.endsWith("CaslonAntique-Bold")) {
            if (textToken.fontSizePt == 14f) {
                return Token.TableHeading(textToken)
            }

            if (textToken.fontSizePt == 19f || textToken.fontSizePt == 22f) {
                return Token.Heading1(textToken)
            }

            if (textToken.fontSizePt == 10f) {
                return Token.TableHeadCell(textToken)
            }
        }

        if (textToken.fontName.endsWith("crossbatstfb") && textToken.text == "h") {
            return Token.CrossIcon
        }

        if (textToken.fontSizePt == 12f && textToken.fontName.endsWith("ACaslonPro-Bold")) {
            return Token.Heading3(textToken)
        }

        if (textToken.fontSizePt == 8f && textToken.fontName.endsWith("ACaslonPro-Regular")) {
            return Token.BodyCellPart(
                text = textToken.text,
                metadata = Token.Metadata(
                    y = textToken.y,
                    height = textToken.height,
                )
            )
        }

        if (textToken.fontSizePt == 9f) {
            if (textToken.fontName.endsWith("ACaslonPro-Bold")) {
                return Token.BoldPart(textToken)
            }

            if (textToken.fontName.endsWith("ACaslonPro-Italic")) {
                return Token.ItalicsPart(textToken)
            }

            if (textToken.fontName.endsWith("ACaslonPro-Regular")) {
                return Token.NormalPart(textToken)
            }
        }

        return null
    }
}