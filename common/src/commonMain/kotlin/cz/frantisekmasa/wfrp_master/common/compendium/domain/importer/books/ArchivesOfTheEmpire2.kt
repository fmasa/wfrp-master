package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.CareerParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.SpellParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextPosition
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.AmmunitionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.ArmourParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.MeleeWeaponsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.RangedWeaponsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description.HeadingDescriptionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TrappingSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.RangedWeaponGroup

object ArchivesOfTheEmpire2 : Book, CareerSource, SpellSource, TrappingSource {
    override val name = "Archives of the Empire — Volume II"
    override val tableFootnotesAsNormalText: Boolean = true

    override fun importCareers(document: Document): List<Career> {
        return CareerParser(
            tokenMapper = {
                when (it) {
                    is Token.BodyCellPart -> Token.NormalPart(it.text)
                    is Token.TableHeadCell -> Token.BoldPart(it.text)
                    else -> it
                }
            },
        )
            .import(
                document,
                this,
                sequenceOf(
                    SocialClass.WARRIORS to 35..35,
                    SocialClass.PEASANTS to 36..36,
                    SocialClass.ACADEMICS to 37..37,
                ),
            )
    }

    override fun importTrappings(document: Document): List<Trapping> {
        val descriptionParser = HeadingDescriptionParser()

        return buildList {
            addAll(
                MeleeWeaponsParser(document, this@ArchivesOfTheEmpire2, descriptionParser)
                    .parse(29, 30..30),
            )
            addAll(
                RangedWeaponsParser(
                    document,
                    this@ArchivesOfTheEmpire2,
                    descriptionParser,
                    rangedWeaponGroupResolver = {
                        if (it.equals("entangle", ignoreCase = true)) {
                            RangedWeaponGroup.ENTANGLING
                        } else {
                            null
                        }
                    },
                )
                    .parse(29, 30..30),
            )
            addAll(
                AmmunitionParser(document, this@ArchivesOfTheEmpire2, descriptionParser)
                    .parse(29, IntRange.EMPTY),
            )
            addAll(
                ArmourParser(
                    document,
                    this@ArchivesOfTheEmpire2,
                    HeadingDescriptionParser(),
                ).parse(29, 30..30),
            )
        }
    }

    override fun importSpells(document: Document): List<Spell> {
        return SpellParser().import(document, this, sequenceOf(32..33))
    }

    override fun resolveToken(textToken: TextToken): Token? {
        if (textToken.fontName.endsWith("CaslonAntique-Bold-SC700")) {
            if (textToken.fontSizePt == 12f || textToken.fontSizePt == 18f) {
                return Token.Heading1(textToken.text)
            }

            return Token.BoxHeader(textToken.text)
        }

        if (textToken.fontName.endsWith("CaslonAntique")) {
            if (textToken.fontSizePt == 11f) {
                return Token.TableHeading(textToken.text)
            }

            if (textToken.fontSizePt == 15f) {
                return Token.BoxHeader(textToken.text)
            }
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

        if (textToken.fontSizePt == 8f) {
            if (textToken.fontName.endsWith("ACaslonPro-Regular")) {
                return Token.BodyCellPart(
                    text = textToken.text,
                    y = textToken.y,
                    height = textToken.height,
                )
            }

            if (textToken.fontName.endsWith("ACaslonPro-Italic")) {
                return Token.ItalicsPart(textToken.text)
            }
        }

        if (textToken.fontSizePt == 9f) {
            if (textToken.fontName.endsWith("ACaslonPro-Bold")) {
                return Token.BoldPart(textToken.text)
            }

            if (textToken.fontName.endsWith("ACaslonPro-Italic")) {
                return Token.ItalicsPart(textToken.text)
            }

            if (textToken.fontName.endsWith("ACaslonPro-Regular")) {
                // These table cells are not formatted as table cells
                if (textToken.text == "Ironfist" || textToken.text == "Big Ogre Club") {
                    return Token.BodyCellPart(
                        text = textToken.text,
                        y = textToken.y,
                        height = textToken.height,
                    )
                }

                return Token.NormalPart(textToken.text)
            }
        }

        return null
    }

    override fun areSameStyle(
        a: TextPosition,
        b: TextPosition,
    ): Boolean {
        return super.areSameStyle(a, b) || arePartsOfHeading2(a, b)
    }

    private fun arePartsOfHeading2(
        a: TextPosition,
        b: TextPosition,
    ): Boolean {
        // Some special symbols have different height
        return a.getFont().getName().endsWith("CaslonAntique-Bold-SC700") &&
            a.getFont().getName().endsWith("CaslonAntique-Bold-SC700") && (
                a.getFontSizeInPt() == b.getFontSizeInPt() || (
                    minOf(a.getFontSizeInPt(), b.getFontSizeInPt()) == 12f &&
                        maxOf(a.getFontSizeInPt(), b.getFontSizeInPt()) == 18f
                )
            )
    }
}
