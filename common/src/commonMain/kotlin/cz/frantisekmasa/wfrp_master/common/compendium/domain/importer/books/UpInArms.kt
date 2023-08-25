package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.CareerParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TalentParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.AmmunitionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.BasicTrappingsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.BasicTrappingsParser.Column
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.MeleeWeaponsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.RangedWeaponsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TrappingSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass

object UpInArms : Book, CareerSource, TalentSource, TrappingSource {

    override val name = "Up in Arms"

    override fun importCareers(document: Document): List<Career> {
        return CareerParser().import(
            document,
            this,
            sequenceOf(
                SocialClass.WARRIORS to listOf(10, 12, 14, 16),
                SocialClass.ACADEMICS to listOf(18),
                SocialClass.RANGERS to listOf(20),
                SocialClass.ACADEMICS to listOf(22),
                SocialClass.WARRIORS to listOf(31, 32, 34, 36, 44, 46, 78),
            ),
        )
    }

    override fun importTalents(document: Document): List<Talent> {
        return TalentParser().import(document, this, (140..141).asSequence())
    }

    override fun importTrappings(document: Document): List<Trapping> {
        val basicTrappingsParser = BasicTrappingsParser(document, this)
        val meleeWeaponsParser = MeleeWeaponsParser(document, this)
        val ammunitionParser = AmmunitionParser(document, this)
        val rangedWeaponsParser = RangedWeaponsParser(document, this)

        return buildList {
            addAll(
                basicTrappingsParser.parse(
                    typeFactory = { null },
                    tablePage = 88,
                    descriptionPages = 88..89,
                    column = Column.LEFT,
                )
            )
            addAll(meleeWeaponsParser.parse(91, 91..91))
            addAll(meleeWeaponsParser.parse(92, 92..92))
            addAll(meleeWeaponsParser.parse(93, 93..93))
            addAll(meleeWeaponsParser.parse(94, 94..95))
            addAll(meleeWeaponsParser.parse(95, 95..96))
            addAll(meleeWeaponsParser.parse(96, 96..97))
            addAll(meleeWeaponsParser.parse(97, 97..97))
            addAll(ammunitionParser.parse(98, IntRange.EMPTY))
            addAll(rangedWeaponsParser.parse(101, 101..103))
            addAll(ammunitionParser.parse(102, 103..104))
        }
    }

    override fun resolveToken(textToken: TextToken): Token? {
        if (textToken.fontName.endsWith("CaslonAntique-Bold-SC700")) {
            return Token.BoxHeader(textToken.text)
        }

        if (textToken.fontName.endsWith("CaslonAntique-Bold")) {
            if (textToken.fontSizePt == 19f) {
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

        if (textToken.fontSizePt == 10f || textToken.fontSizePt == 9f) {
            if (textToken.fontName.endsWith("ACaslonPro-Bold")) {
                if (textToken.height == 6.201f) {
                    return Token.TableHeadCell(textToken.text)
                }

                return Token.BoldPart(textToken.text)
            }

            if (textToken.fontName.endsWith("ACaslonPro-Italic")) {
                return Token.ItalicsPart(textToken.text)
            }

            if (textToken.fontName.endsWith("ACaslonPro-Regular")) {
                if (textToken.height == 6.201f) {
                    return Token.BodyCellPart(textToken.text)
                }

                return Token.NormalPart(textToken.text)
            }

            if (textToken.fontName.endsWith("ACaslonPro-BoldItalic")) {
                return Token.BoldItalicPart(textToken.text)
            }
        }

        return null
    }
}
