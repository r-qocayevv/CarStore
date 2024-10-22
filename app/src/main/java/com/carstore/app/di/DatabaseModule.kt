package com.carstore.app.di

import android.content.Context
import androidx.room.Room
import com.carstore.app.db.CarsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase (@ApplicationContext context: Context) = Room
        .databaseBuilder(context, CarsDatabase::class.java, "brands_and_models_database")
        .build()

    @Provides
    @Singleton
    fun provideDao(database : CarsDatabase) = database.carsDao()

}