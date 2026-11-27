# Règles R8 de la version « release » (Play Store, plus tard).
# L'APK distribué aujourd'hui est la version « debug » : ces règles ne le touchent pas.

# Pinyin automatique (Mes mots) : pinyin4j lit ses tables par réflexion et ressources du .jar
-keep class net.sourceforge.pinyin4j.** { *; }
-keep class com.hp.hpl.sparta.** { *; }
-dontwarn net.sourceforge.pinyin4j.**
-dontwarn com.hp.hpl.sparta.**

# Lecture du chinois sur une photo (ML Kit, services Google) et téléchargement du module
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_text_common.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_common.** { *; }
-dontwarn com.google.mlkit.**
-dontwarn com.google.android.gms.**

# Rappels et notifications : WorkManager crée les « workers » par leur nom, Android les « receivers »
-keep class com.zhuzi.app.reminder.** extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.zhuzi.app.reminder.** extends android.content.BroadcastReceiver { *; }

# Messages d'erreur lisibles (numéros de ligne)
-keepattributes SourceFile,LineNumberTable
