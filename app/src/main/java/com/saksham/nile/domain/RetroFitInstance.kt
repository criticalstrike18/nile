package com.saksham.nile.domain

import com.saksham.nile.BuildConfig
import com.saksham.nile.data.StockApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("apiKey")
    fun provideApiKey(): String = BuildConfig.API_KEY

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://finnhub.io/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideStockApiService(retrofit: Retrofit): StockApiService {
        return retrofit.create(StockApiService::class.java)
    }
}