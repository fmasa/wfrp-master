package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType.MeleeWeapon
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
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description.ListDescriptionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TrappingSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.MeleeWeaponGroup

object UpInArms : Book, CareerSource, TalentSource, TrappingSource {
    override val name = "Up in Arms"
    override val tokensSorted: Boolean = false

    override fun importCareers(document: Document): List<Career> {
        return CareerParser(convertTablesToText = true).import(
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
        val descriptionParser = ListDescriptionParser()
        val basicTrappingsParser = BasicTrappingsParser(document, this, descriptionParser)
        val meleeWeaponsParser = MeleeWeaponsParser(document, this, descriptionParser)
        val ammunitionParser = AmmunitionParser(document, this, descriptionParser)
        val rangedWeaponsParser = RangedWeaponsParser(document, this, descriptionParser)

        return sequence {
            yieldAll(
                basicTrappingsParser.parse(
                    typeFactory = { null },
                    tablePage = 88,
                    descriptionPages = 88..89,
                    column = Column.LEFT,
                ),
            )
            yieldAll(meleeWeaponsParser.parse(91, 91..91))
            yieldAll(meleeWeaponsParser.parse(92, 92..92))
            yieldAll(meleeWeaponsParser.parse(93, 93..93))
            yieldAll(meleeWeaponsParser.parse(94, 94..95))
            yieldAll(meleeWeaponsParser.parse(95, 95..96))
            yieldAll(meleeWeaponsParser.parse(96, 96..97))
            yieldAll(meleeWeaponsParser.parse(97, 97..97))
            yieldAll(ammunitionParser.parse(98, IntRange.EMPTY))
            yieldAll(rangedWeaponsParser.parse(101, 101..103))
            yieldAll(ammunitionParser.parse(102, 103..104))
        }.map {
            if (
                it.name == "Warhammer" &&
                (it.trappingType as MeleeWeapon).group == MeleeWeaponGroup.BASIC
            ) {
                it.copy(name = "Warhammer (Basic)")
            } else {
                it
            }
        }.toList()
    }

    override fun resolveToken(textToken: TextToken): Token? {
        if (textToken.fontName.endsWith("CaslonAntique-Bold-SC700")) {
            return Token.BoxHeader(textToken.text)
        }

        if (textToken.fontName.endsWith("CaslonAntique-Bold")) {
            if (textToken.fontSizePt == 19f || textToken.fontSizePt == 22f) {
                return Token.Heading1(textToken.text)
            }

            if (textToken.fontSizePt == 10f) {
                return Token.TableHeading(textToken.text)
            }

            if (textToken.fontSizePt == 15f) {
                return Token.BoxHeader(textToken.text)
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
                    return Token.BodyCellPart(
                        text = textToken.text,
                        y = textToken.y,
                        height = textToken.height,
                    )
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
