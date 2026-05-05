-keepattributes SourceFile,LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

# Hilt
-keepclassmembers class * { @javax.inject.Inject <init>(...); }
-keepnames class * implements dagger.MembersInjector
-keep class * implements dagger.internal.Factory

# Room
-keep class androidx.room.RoomDatabase { public <methods>; }
-keep class androidx.room.TypeConverter { public <methods>; }
-keep class * extends androidx.room.migration.Migration { <init>(...); }
-dontwarn androidx.room.**

# For timber
-keep class timber.log.Timber { *; }

# For WorkManager
-keep public class * extends androidx.work.ListenableWorker {
    <init>(android.content.Context,androidx.work.WorkerParameters);
}

# For AdMob
-keep public class com.google.android.gms.ads.** { *; }
-keep public class com.google.ads.** { *; }
-keep public class com.google.firebase.analytics.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn com.google.ads.**
-dontwarn com.google.firebase.**