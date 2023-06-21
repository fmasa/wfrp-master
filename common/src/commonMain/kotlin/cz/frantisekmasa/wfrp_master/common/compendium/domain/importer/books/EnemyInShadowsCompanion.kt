package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.SpellParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextPosition
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource

object EnemyInShadowsCompanion : Book, SpellSource {

    override val name = "Enemy in Shadows - Companion"

    private const val pageOffset = 2

    override fun importSpells(document: Document): List<Spell> {
        return SpellParser(
            specialLores = mapOf(
                "Chaos Arcane Spells" to setOf(
                    SpellLore.NURGLE,
                    SpellLore.SLAANESH,
                    SpellLore.TZEENTCH,
                ),
            ),
            ignoredSpellLikeHeadings = setOf("Lore Attribute"),
        ).import(
            document,
            this,
            pageRanges = sequenceOf(79 + pageOffset..83 + pageOffset)
        ).map { it.copy(isVisibleToPlayers = false) }
    }

    override fun areSameStyle(a: TextPosition, b: TextPosition): Boolean {
        return super.areSameStyle(a, b) || arePartsOfHeading2(a, b)
    }

    private fun arePartsOfHeading2(a: TextPosition, b: TextPosition): Boolean {
        // Some special symbols have different height
        return a.getFont().getName().endsWith("CaslonAntique-Bold-SC700") &&
            a.getFont().getName().endsWith("CaslonAntique-Bold-SC700") && (
            a.getFontSizeInPt() == b.getFontSizeInPt() || (
                minOf(a.getFontSizeInPt(), b.getFontSizeInPt()) == 12f &&
                    maxOf(a.getFontSizeInPt(), b.getFontSizeInPt()) == 18f
                )
            )
    }

    override fun resolveToken(textToken: TextToken): Token? {
        if (
            (textToken.fontSizePt == 12f || textToken.fontSizePt == 18f) &&
            textToken.fontName.endsWith("CaslonAntique-Bold-SC700")
        ) {
            return Token.Heading2(textToken.text)
        }

        if (
            (textToken.fontSizePt == 19f || textToken.fontSizePt == 22f) &&
            textToken.fontName.endsWith("CaslonAntique-Bold")
        ) {
            return Token.Heading2(textToken.text)
        }

        if (textToken.fontSizePt == 12f && textToken.fontName.endsWith("ACaslonPro-Bold")) {
            return Token.Heading3(textToken.text)
        }

        if (textToken.fontSizePt == 10f || textToken.fontSizePt == 9f) {
            if (textToken.fontName.endsWith("ACaslonPro-Bold")) {
                return Token.BoldPart(textToken.text)
            }

            if (textToken.fontName.endsWith("ACaslonPro-Italic")) {
                return Token.ItalicsPart(textToken.text)
            }
            if (textToken.fontName.endsWith("ACaslonPro-Regular")) {
                return Token.NormalPart(textToken.text)
            }
        }

        return null
    }
}
