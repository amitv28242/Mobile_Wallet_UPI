# ==================================================
# BuildConfig
# ==================================================
-keep class com.mobilewallet.consumer.BuildConfig { *; }

# ==================================================
# Models (Gson serialization)
# ==================================================
-keep class com.mobilewallet.consumer.models.** { *; }
-keep class com.mobilewallet.consumer.api.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# ==================================================
# Retrofit
# ==================================================
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# ==================================================
# Gson
# ==================================================

#-dontwarn com.google.gson.**
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# ==================================================
# Firebase
# ==================================================
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# ==================================================
# ZXing
# ==================================================
-keep class com.google.zxing.** { *; }
-dontwarn com.google.zxing.**

# ==================================================
# CameraX
# ==================================================
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# ==================================================
# ML Kit
# ==================================================
-keep class com.google.mlkit.** { *; }
-dontwarn com.google.mlkit.**

# Lifecycle
-keep class androidx.lifecycle.** { *; }

# ==================================================
# Lottie
# ==================================================
-dontwarn com.airbnb.lottie.**

# ==================================================
# MPAndroidChart
# ==================================================
-keep class com.github.mikephil.charting.** { *; }

# ==================================================
# Shimmer
# ==================================================
-keep class com.facebook.shimmer.** { *; }

# ==================================================
# Room
# ==================================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ==================================================
# Serializable
# ==================================================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}