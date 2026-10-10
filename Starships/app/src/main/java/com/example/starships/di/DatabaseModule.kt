package com.example.starships.di

import android.content.Context
import androidx.room.Room
import com.example.starships.data.local.FavoritesDao
import com.example.starships.data.local.StarshipsDatabase
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): StarshipsDatabase {
        return Room.databaseBuilder(
            context,
            StarshipsDatabase::class.java,
            "starships.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFavoritesDao(
        database: StarshipsDatabase
    ): FavoritesDao {
        return database.favoritesDao()
    }
}