# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn com.google.android.gms.**
-dontwarn org.apache.harmony.**
-dontwarn test.Lib.**
-dontwarn retrofit2.**
-dontwarn org.simpleframework.**
-dontwarn okio.**
-dontwarn javax.**
-dontwarn com.sun.**
-dontwarn com.itextpdf.**
-dontwarn com.squareup.picasso.**
-dontwarn java.awt.**
-dontwarn org.jetbrains.annotations.**
-keep class org.xmlpull.v1.** { *;}
-dontwarn org.xmlpull.v1.**
-dontwarn org.stringtemplate.**
-dontwarn org.antlr.**
-dontwarn com.google.auto.**
-dontwarn org.apache.log4j.**

-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception



-dontwarn com.instagram.common.json.**
-dontwarn com.fasterxml.jackson.databind.ext.DOMSerializer
-dontwarn com.squareup.javawriter.JavaWriter
-dontwarn com.google.common.primitives.UnsignedBytes*

-dontwarn org.apache.commons.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**

#-itextpdf
-keep class com.itextpdf.** { *; }

#-keep class com.salenlock.okdollar.model.AddCashier.** { *; }
#-keep class com.salenlock.okdollar.ui.** { *; }

-dontwarn com.eficaz_android.fitbet.model.**
-keep class com.eficaz_android.fitbet.model.**{*;}

# Android logging.
-assumenosideeffects class android.util.Log {
   public static boolean isLoggable(java.lang.String, int);
   public static int v(...);
   public static int i(...);
   public static int w(...);
   public static int d(...);
   public static int e(...);
   public static java.lang.String getStackTraceString(java.lang.Throwable);
}

# This is dangerous: it removes ALL println calls, not just logging.
-assumenosideeffects class java.io.PrintStream {
     public void println(%);
     public void println(**);
     public void print(%);
     public void print(**);
}