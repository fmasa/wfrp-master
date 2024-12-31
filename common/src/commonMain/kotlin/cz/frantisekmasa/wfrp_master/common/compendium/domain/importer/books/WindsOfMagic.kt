package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.CareerParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.SpellParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextPosition
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.BasicTrappingsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.BasicTrappingsParser.Column
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description.ListDescriptionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TrappingSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass

object WindsOfMagic : Book, CareerSource, SpellSource, TrappingSource {
    override val name = "Winds of Magic"

    override fun importCareers(document: Document): List<Career> {
        return CareerParser().import(
            document,
            this,
            sequenceOf(
                SocialClass.WARRIORS to listOf(36),
                SocialClass.ACADEMICS to listOf(38),
                SocialClass.PEASANTS to listOf(42),
                SocialClass.ACADEMICS to listOf(40, 56, 68, 80, 92, 104, 116, 128, 140),
            ),
        )
    }

    override fun importSpells(document: Document): List<Spell> {
        return SpellParser(
            specialLores =
                mapOf(
                    "New Arcane Spells" to SpellLore.values().toSet() - SpellLore.PETTY,
                ),
            isEnd = {
                (
                    it is Token.Heading1 &&
                        it.text.trim()
                            .equals("Ritual Magic", ignoreCase = true)
                ) ||
                    it == null
            },
        ).import(
            document,
            this,
            pageRanges =
                sequenceOf(
                    26..27,
                    62..65,
                    74..77,
                    86..89,
                    98..101,
                    110..113,
                    122..125,
                    134..137,
                    146..149,
                ),
        )
    }

    override fun importTrappings(document: Document): List<Trapping> {
        val basicTrappingsParser =
            BasicTrappingsParser(
                document,
                this,
                ListDescriptionParser(),
            )

        return buildList {
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.ClothingOrAccessory },
                    151,
                    151..151,
                    Column.LEFT,
                ),
            )
        }
    }

    override fun areSameStyle(
        a: TextPosition,
        b: TextPosition,
    ): Boolean {
        return super.areSameStyle(a, b) || arePartsOfNormalText(a, b)
    }

    private fun arePartsOfNormalText(
        a: TextPosition,
        b: TextPosition,
    ): Boolean {
        // Some special symbols have different height
        return a.getFont().getName().endsWith("ACaslonPro-Regular") &&
            a.getFont().getName().endsWith("ACaslonPro-Bold") &&
            a.getFontSizeInPt() == b.getFontSizeInPt() &&
            a.getUnicode() == " "
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
                return Token.TableHeadCell(textToken.text)
            }
        }

        if (textToken.fontName.endsWith("crossbatstfb") && textToken.text == "h") {
            return Token.CrossIcon
        }

        if (textToken.fontSizePt == 12f && textToken.fontName.endsWith("ACaslonPro-Bold")) {
            return Token.Heading3(textToken.text)
        }

        if (textToken.fontSizePt == 9f && textToken.fontName.endsWith("ACaslonPro-Regular")) {
            return Token.BodyCellPart(
                text = textToken.text,
                y = textToken.y,
                height = textToken.height,
            )
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
