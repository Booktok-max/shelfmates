# ─────────────────────────────────────────────────────────────────────────────
# Shelfmates R8 / ProGuard rules
#
# Enable via `isMinifyEnabled = true` in the release build type.
# Verified by the `minifyReleaseWithR8` task in CI; see .github/workflows/ci.yml.
#
# Only rules that are actually load-bearing for THIS app are listed. Firebase,
# OkHttp, Coil and Room all ship their own consumer rules, so they are not
# duplicated here.
# ─────────────────────────────────────────────────────────────────────────────

# Keep line numbers and the original file name for release crash reports.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile


# ── Retrofit ─────────────────────────────────────────────────────────────────
# Retrofit synthesises its API implementation from the interface's generic
# signatures and method annotations at runtime. R8 cannot see those uses, so
# the signature and annotation attributes must survive.
-keepattributes Signature,InnerClasses,EnclosingMethod
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep the service interfaces themselves: every method on them is an endpoint.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>


# ── Moshi ────────────────────────────────────────────────────────────────────
# CRITICAL: this app uses KotlinJsonAdapterFactory (see GoogleBooksService),
# which is Moshi's *reflection* adapter, not the KSP-generated one. The
# generated adapters on the classpath are irrelevant here — the reflection
# adapter resolves properties from Kotlin metadata at runtime. Obfuscating a
# model's property names therefore changes the JSON keys it reads.
#
# The long-term fix is to drop KotlinJsonAdapterFactory and rely on the
# already-wired KSP codegen (moshi-kotlin-codegen). Until then, the model
# package is kept intact.
-keep class com.shelfmates.data.model.** { *; }
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
}


# ── epublib (com.positiondev.epublib:epublib-core) ───────────────────────────
# Note the package is still `nl.siegmann.epublib` — the com.positiondev
# coordinates are a fork, not a rename. Used by EpubParser and the in-app
# reader. epublib resolves its own resource/parser types reflectively, so the
# whole package is kept.
-keep class nl.siegmann.epublib.** { *; }
-dontwarn nl.siegmann.epublib.**
-dontwarn org.xmlpull.v1.**


# ── Logging interceptor ──────────────────────────────────────────────────────
# HttpLoggingInterceptor logs at the named level; keep the referenced constants.
-keep class okhttp3.internal.http.** { *; }
-dontwarn okhttp3.internal.platform.**


# ── Suppress warnings for optional/compile-only dependencies ─────────────────
# R8 warns on references these libraries make reflectively to classes absent at
# runtime. None are reached on any code path this app exercises.
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn java.lang.invoke.**
-dontwarn javax.annotation.**
