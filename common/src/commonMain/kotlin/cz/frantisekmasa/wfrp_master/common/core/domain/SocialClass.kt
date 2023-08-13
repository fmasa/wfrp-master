package cz.frantisekmasa.wfrp_master.common.core.domain

import cz.frantisekmasa.wfrp_master.common.Str
import dev.icerock.moko.resources.StringResource

enum class SocialClass(
    override val translatableName: StringResource,
) : NamedEnum {
    ACADEMICS(Str.social_classes_academics),
    BURGHERS(Str.social_classes_burghers),
    COURTIERS(Str.social_classes_courtiers),
    PEASANTS(Str.social_classes_peasants),
    RANGERS(Str.social_classes_rangers),
    RIVERFOLK(Str.social_classes_riverfolk),
    ROGUES(Str.social_classes_rogues),
    WARRIORS(Str.social_classes_warriors),
}
