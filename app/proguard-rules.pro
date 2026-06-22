# ProGuard rules for NITHA
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
