-keep class com.zuyceo.levelupbot.ProxyConfig { *; }
-keepclassmembers class com.zuyceo.levelupbot.ProxyConfig {
    private static final int[] H;
    private static final int P;
}
-dontwarn com.zuyceo.levelupbot.ProxyConfig

-keep class com.zuyceo.levelupbot.** { *; }
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

-obfuscatedictionary proguard-dict.txt
