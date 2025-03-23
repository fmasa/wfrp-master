-keep class com.revenuecat.purchases.** { *; }

-keep class cz.frantisekmasa.wfrp_master.**.R$drawable { *; }
-keep class cz.frantisekmasa.wfrp_master.**.R$raw { *; }

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

# Voyager
-keepnames class * implements cafe.adriel.voyager.core.screen.Screen

# Kodein
-keepattributes Signature

-keep, allowobfuscation, allowoptimization class org.kodein.type.TypeReference
-keep, allowobfuscation, allowoptimization class org.kodein.type.JVMAbstractTypeToken$Companion$WrappingTest

-keep, allowobfuscation, allowoptimization class * extends org.kodein.type.TypeReference
-keep, allowobfuscation, allowoptimization class * extends org.kodein.type.JVMAbstractTypeToken$Companion$WrappingTest

# See https://github.com/kosi-libs/Kodein/issues/205#issuecomment-510069695
-keep, allowobfuscation, allowoptimization interface cz.frantisekmasa.wfrp_master.**

-dontwarn com.gemalto.jp2.JP2Decoder
-dontwarn org.slf4j.impl.StaticLoggerBinder