# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ares/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
-dontoptimize

-keep class org.apache.log4j.** { *; }
-dontwarn org.apache.log4j.**
-dontnote org.apache.log4j.**

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable,Exceptions,InnerClasses,Signature,Deprecated,EnclosingMethod

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
-keepparameternames

# Preserve all annotations.
-keepattributes *Annotation*

# Preserve all .class method names.
-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.
-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class idv.ares.net.restapi.service.ApiInterceptor {public protetcted *;}
-keep public interface idv.ares.net.restapi.service.ApiInterceptorObserver {<methods>;}
-keep public class idv.ares.net.restapi.service.NetworkConnectionInterceptor {public <methods>;}
-keep public interface idv.ares.net.restapi.service.ResponseCallback { <methods>;}
-keep public class idv.ares.net.restapi.service.RestMethodResult { <methods>;}
-keep public class idv.ares.util.** {public protected *;}
