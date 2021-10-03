-keep class com.revenuecat.purchases.** { *; }

# Optional dependency used for PDF reader when reading encrypted PDFs
-dontnote com.lowagie.bouncycastle.*

#
# Kotlinx serialization
#
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class cz.frantisekmasa.wfrp_master.**$$serializer { *; }
-keepclassmembers class cz.frantisekmasa.wfrp_master.** {
    *** Companion;
}
-keepclasseswithmembers class cz.frantisekmasa.wfrp_master.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class cz.muni.fi.rpg.**$$serializer { *; }
-keepclassmembers class cz.muni.fi.rpg.** {
    *** Companion;
}
-keepclasseswithmembers class cz.muni.fi.rpg.** {
    kotlinx.serialization.KSerializer serializer(...);
}