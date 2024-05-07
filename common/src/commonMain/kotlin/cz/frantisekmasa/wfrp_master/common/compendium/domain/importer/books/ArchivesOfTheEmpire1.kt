package cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.books

import cz.frantisekmasa.wfrp_master.common.compendium.domain.Career
import cz.frantisekmasa.wfrp_master.common.compendium.domain.Trapping
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.CareerParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Document
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.TextToken
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.Token
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.AmmunitionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.MeleeWeaponsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.RangedWeaponsParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.parsers.trappings.description.HeadingDescriptionParser
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.CareerSource
import cz.frantisekmasa.wfrp_master.common.compendium.domain.importer.sources.TrappingSource
import cz.frantisekmasa.wfrp_master.common.core.domain.SocialClass

object ArchivesOfTheEmpire1 : Book, CareerSource, TrappingSource {
    override val name = "Archives of the Empire — Volume I"
    override val tableFootnotesAsNormalText: Boolean = true

    override fun importCareers(document: Document): List<Career> {
        return CareerParser()
            .import(
                document,
                this,
                sequenceOf(
                    SocialClass.RANGERS to 88..90,
                    SocialClass.WARRIORS to 91..91,
                ),
            )
    }

    override fun importTrappings(document: Document): List<Trapping> {
        val descriptionParser = HeadingDescriptionParser()

        return buildList {
            addAll(
                MeleeWeaponsParser(document, this@ArchivesOfTheEmpire1, descriptionParser)
                    .parse(92, 94..94),
            )
            addAll(
                RangedWeaponsParser(document, this@ArchivesOfTheEmpire1, descriptionParser)
                    .parse(93, 94..94),
            )
            addAll(
                AmmunitionParser(document, this@ArchivesOfTheEmpire1, descriptionParser)
                    .parse(93, 94..94),
            )
        }
    }

    override fun resolveToken(textToken: TextToken): Token? {
        if (textToken.fontName.endsWith("CaslonAntique-Bold-SC700")) {
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

        if (textToken.fontSizePt == 8f && textToken.fontName.endsWith("ACaslonPro-Regular")) {
            return Token.BodyCellPart(
                text = textToken.text,
                y = textToken.y,
                height = textToken.height,
            )
        }

        if (textToken.fontSizePt == 9f) {
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
