package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.BlessingParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.CareerParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.MiracleParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.SkillParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.SpellParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TalentParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextPosition
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TraitParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.BlessingSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.MiracleSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SkillSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TraitSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object CoreRulebook :
    Book,
    CareerSource,
    SkillSource,
    TalentSource,
    TraitSource,
    SpellSource,
    BlessingSource,
    MiracleSource {

    override val name: String = "Core Rulebook"

    override fun importTalents(document: Document): List<Talent> {
        return TalentParser().import(document, this, (132..147).asSequence())
    }

    override fun importSkills(document: Document): List<Skill> {
        return SkillParser().import(
            document,
            this,
            sequenceOf(
                118..124,
                126..131,
            ).flatten(),
        )
    }

    override fun importCareers(document: Document): List<Career> {
        return CareerParser().import(
            document,
            this,
            sequenceOf(
                SocialClass.ACADEMICS to 53..60,
                SocialClass.BURGHERS to 61..68,
                SocialClass.COURTIERS to 69..76,
                SocialClass.PEASANTS to 77..84,
                SocialClass.RANGERS to 85..92,
                SocialClass.RIVERFOLK to 93..100,
                SocialClass.ROGUES to 101..108,
                SocialClass.WARRIORS to 109..116,
            ),
        )
    }

    override fun importTraits(document: Document): List<Trait> {
        return TraitParser().import(document, this, (338..343).asSequence())
    }

    override fun importSpells(document: Document): List<Spell> {
        return SpellParser().import(document, this, sequenceOf(240..257))
    }

    override fun importBlessings(document: Document): List<Blessing> {
        return BlessingParser().import(document, this, 221)
    }

    override fun importMiracles(document: Document): List<Miracle> {
        return MiracleParser().import(document, this, (222..228).asSequence())
    }

    override fun areSameStyle(a: TextPosition, b: TextPosition): Boolean {
        return super.areSameStyle(a, b) ||
            arePartsOfHeading2(a, b) ||
            arePartsOfHeading3(a, b)
    }

    private fun arePartsOfHeading2(a: TextPosition, b: TextPosition): Boolean {
        // Some headings are a mix of 12pt and 18pt font
        return a.getFont().getName() == b.getFont().getName() &&
            a.getFont().getName() == "CaslonAntique-Bold-SC700" &&
            min(a.getFontSizeInPt(), b.getFontSizeInPt()) == 12f &&
            max(a.getFontSizeInPt(), b.getFontSizeInPt()) == 18f
    }

    private fun arePartsOfHeading3(a: TextPosition, b: TextPosition): Boolean {
        // Heading 3 may contain Italics
        return a.getFont().getName() == b.getFont().getName() &&
            a.getFont().getName() == "ACaslonPro-Bold" &&
            a.getFontSizeInPt() == b.getFontSizeInPt() &&
            a.getFontSizeInPt() == 12f
    }

    override fun resolveToken(textToken: TextToken): Token? {
        if (textToken.fontName == "CaslonAntique" && textToken.fontSizePt == 15f) {
            return Token.OptionsBoxHeading(textToken.text)
        }

        if (textToken.fontName == "CaslonAntique,Bold" && textToken.fontSizePt == 19f) {
            return Token.Heading1(textToken.text)
        }

        if (textToken.fontName == "CaslonAntique-Bold-SC700") {
            if (textToken.fontSizePt == 12f || textToken.fontSizePt == 18f) {
                return Token.Heading2(textToken.text)
            }

            return Token.BoxHeader(textToken.text)
        }

        if (textToken.fontName == "ACaslonPro-Bold" && textToken.fontSizePt == 12f) {
            return Token.Heading3(textToken.text)
        }

        if (textToken.fontName.endsWith("HPBTHP+ACaslonPro-Regular") && textToken.text.startsWith("•")) {
            return Token.BulletPoint
        }

        if (textToken.fontSizePt == 9.0f) {
            if (textToken.fontName == "ACaslonPro-Bold") {
                return Token.BoldPart(textToken.text)
            }

            if (textToken.fontName == "ACaslonPro-Regular") {
                if (heightEquals(textToken.height, 6.39f)) {
                    return Token.ItalicsPart(textToken.text)
                }

                if (heightEquals(textToken.height, 6.2f)) {
                    return Token.NormalPart(textToken.text)
                }
            }
        }

        return null
    }

    private fun heightEquals(first: Float, second: Float): Boolean {
        return abs(first - second) < 0.01
    }
}
