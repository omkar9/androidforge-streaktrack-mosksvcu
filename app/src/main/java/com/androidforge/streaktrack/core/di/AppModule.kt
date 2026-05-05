package com.androidforge.streaktrack.core.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.androidforge.streaktrack.core.util.Constants
import com.androidforge.streaktrack.data.local.AppDatabase
import com.androidforge.streaktrack.data.local.dao.HabitDao
import com.androidforge.streaktrack.presentation.common.AdInterstitialManager
import com.google.android.gms.ads.MobileAds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideHabitDao(db: AppDatabase): HabitDao {
        return db.habitDao()
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideAdInterstitialManager(@ApplicationContext context: Context): AdInterstitialManager {
        // Initialize MobileAds here if not already done in Application class
        MobileAds.initialize(context) {}
        return AdInterstitialManager(context)
    }
}