package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Blessing
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Miracle
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Skill
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Spell
import cz.frantisekmasa.wfrp_master.common.compendium.domain.SpellLore
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Talent
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trait
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.TrappingType
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
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.AmmunitionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.ArmourParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.BasicTrappingsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.BasicTrappingsParser.Column
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.MeleeWeaponsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.RangedWeaponsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.BlessingSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.MiracleSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SkillSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.SpellSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TalentSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TraitSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TrappingSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass
import cz.frantisekmasa.wfrp_master.common.core.domain.trappings.Encumbrance
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
    MiracleSource,
    TrappingSource {

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
        return SpellParser(
            specialLores = mapOf(
                "Petty Spells" to setOf(SpellLore.PETTY),
                "Arcane Spells" to SpellLore.values().toSet() - SpellLore.PETTY,
            ),
        ).import(document, this, sequenceOf(240..257))
    }

    override fun importBlessings(document: Document): List<Blessing> {
        return BlessingParser().import(document, this, 221)
    }

    override fun importMiracles(document: Document): List<Miracle> {
        return MiracleParser().import(document, this, (222..228).asSequence())
    }

    override fun importTrappings(document: Document): List<Trapping> {
        val structure = this
        return buildList {
            addAll(
                MeleeWeaponsParser(document, structure).parse(294, IntRange.EMPTY)
            )
            addAll(
                RangedWeaponsParser(document, structure).parse(295, IntRange.EMPTY)
            )
            addAll(
                AmmunitionParser(document, structure).parse(296, IntRange.EMPTY)
            )
            addAll(
                ArmourParser().parse(document, structure, 300, IntRange.EMPTY)
            )

            val basicTrappingsParser = BasicTrappingsParser(document, structure)
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.Container(carries = Encumbrance(it.toDouble())) },
                    301,
                    301..301,
                    Column.LEFT,
                    additionalColumn = 3,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.ClothingOrAccessory },
                    302,
                    302..302,
                    Column.LEFT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.FoodOrDrink },
                    302,
                    302..303,
                    Column.RIGHT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.ToolOrKit },
                    303,
                    303..304,
                    Column.LEFT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.ToolOrKit },
                    303,
                    303..304,
                    Column.RIGHT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.BookOrDocument },
                    304,
                    304..305,
                    Column.LEFT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.TradeTools },
                    305,
                    IntRange.EMPTY, // There are only descriptions for specific professions
                    Column.LEFT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.DrugOrPoison },
                    306,
                    306..307,
                    Column.RIGHT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.HerbOrDraught },
                    307,
                    307..307,
                    Column.LEFT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    { TrappingType.Prosthetic },
                    308,
                    308..308,
                    Column.LEFT,
                )
            )
            addAll(
                basicTrappingsParser.parse(
                    typeFactory = { null },
                    308,
                    309..309,
                    Column.RIGHT,
                )
            )
        }.sortedBy { it.name }
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
            return Token.OptionsBoxHeading
        }

        if (textToken.fontName == "CaslonAntique,Bold") {
            if (textToken.fontSizePt == 19f) {
                return Token.Heading1(textToken.text)
            }

            if (textToken.fontSizePt == 10f && textToken.text.isNotBlank()) {
                return Token.TableHeadCell(textToken.text)
            }
        }

        if (textToken.fontName == "crossbatstfb" && textToken.text == "h") {
            return Token.CrossIcon
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

        if (textToken.fontSizePt == 8f && textToken.fontName.endsWith("ACaslonPro-Regular")) {
            return Token.BodyCellPart(
                text = textToken.text,
                y = textToken.y,
                height = textToken.height,
            )
        }

        if (textToken.fontSizePt == 11f && textToken.fontName.endsWith("CaslonAntique")) {
            return Token.TableHeading(textToken.text)
        }

        return null
    }

    private fun heightEquals(first: Float, second: Float): Boolean {
        return abs(first - second) < 0.01
    }
}
