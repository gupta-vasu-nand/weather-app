package com.weatherapp.di

import com.google.gson.Gson
import com.weatherapp.data.local.db.AppDatabase
import com.weatherapp.data.local.datastore.SettingsDataStore
import com.weatherapp.data.remote.source.RemoteDataSource
import com.weatherapp.data.repository.WeatherRepositoryImpl
import com.weatherapp.domain.repository.WeatherRepository
import com.weatherapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        database: AppDatabase,
        remoteDataSource: RemoteDataSource,
        settingsDataStore: SettingsDataStore,
        cacheManager: com.weatherapp.utils.WeatherCacheManager,
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): WeatherRepository {
        return WeatherRepositoryImpl(
            database = database,
            remoteDataSource = remoteDataSource,
            settingsDataStore = settingsDataStore,
            cacheManager = cacheManager,
            gson = Gson(),
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideGetCurrentWeatherUseCase(
        repository: WeatherRepository
    ): GetCurrentWeatherUseCase {
        return GetCurrentWeatherUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetSavedCitiesUseCase(
        repository: WeatherRepository
    ): GetSavedCitiesUseCase {
        return GetSavedCitiesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSaveCityUseCase(
        repository: WeatherRepository
    ): SaveCityUseCase {
        return SaveCityUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteCityUseCase(
        repository: WeatherRepository
    ): DeleteCityUseCase {
        return DeleteCityUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchCityUseCase(
        repository: WeatherRepository
    ): SearchCityUseCase {
        return SearchCityUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdatePreferencesUseCase(
        repository: WeatherRepository
    ): UpdatePreferencesUseCase {
        return UpdatePreferencesUseCase(repository)
    }
}