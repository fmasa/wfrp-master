package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Disease
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.CareerParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.DiseaseParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.MiracleParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.SpellParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TalentParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextPosition
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.DiseaseSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.MiracleSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import kotlin.math.max
import kotlin.math.min

object SeaOfClaws : Book, SpellSource, TalentSource, CareerSource, MiracleSource, DiseaseSource {
    override val name = "Sea of Claws"

    override fun importSpells(document: Document): List<Spell> {
        return SpellParser().import(document, this, sequenceOf(15..15))
    }

    override fun importTalents(document: Document): List<Talent> {
        return TalentParser().import(document, this, (63..63).asSequence())
    }

    override fun importCareers(document: Document): List<Career> {
        return CareerParser(convertTablesToText = true).import(
            document,
            this,
            sequenceOf(
                SocialClass.SEAFARERS to listOf(64, 68, 70, 72, 74, 76, 78, 90),
            ),
        ) +
            CareerParser(convertTablesToText = true, hasAttributesInRightColumn = true).import(
                document,
                this,
                sequenceOf(
                    SocialClass.SEAFARERS to listOf(66),
                ),
            )
    }

    override fun importMiracles(document: Document): List<Miracle> {
        return MiracleParser().import(document, this, sequenceOf(87, 91))
    }

    override fun importDiseases(document: Document): List<Disease> {
        return DiseaseParser(convertTablesToText = true)
            .import(document, this, sequenceOf(125))
    }

    override fun areSameStyle(
        a: TextPosition,
        b: TextPosition,
    ): Boolean {
        return super.areSameStyle(a, b) ||
            arePartsOfHeading2(a, b)
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
        if (textToken.fontSizePt == 10f) {
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

        if (textToken.fontName.endsWith("CaslonAntique-Bold")) {
            if (textToken.fontSizePt == 22f) {
                return Token.Heading1(textToken.text)
            }

            if (textToken.fontSizePt == 10f) {
                return Token.TableHeading(textToken.text)
            }
        }

        if (textToken.fontName.endsWith("crossbatstfb") && textToken.text == "h") {
            return Token.CrossIcon
        }

        if (textToken.fontSizePt == 12f && textToken.fontName.endsWith("ACaslonPro-Bold")) {
            return Token.Heading3(textToken.text)
        }

        if (textToken.fontSizePt == 9f) {
            if (textToken.fontName.endsWith("ACaslonPro-Regular")) {
                return Token.BodyCellPart(
                    text = textToken.text,
                    y = textToken.y,
                    height = textToken.height,
                )
            }

            if (textToken.fontName.endsWith("ACaslonPro-Bold")) {
                return Token.TableHeadCell(textToken.text)
            }
        }

        if (textToken.fontName.endsWith("CaslonAntique-Bold-SC700")) {
            if (textToken.fontSizePt == 12f || textToken.fontSizePt == 18f) {
                return Token.Heading2(textToken.text)
            }

            return Token.BoxHeader(textToken.text)
        }

        return null
    }
}
