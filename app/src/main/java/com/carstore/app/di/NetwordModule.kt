package com.carstore.app.di

import com.carstore.app.util.Constants.Companion.BASE_URL
import com.carstore.app.api.CarBrandsAndModelsAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetwordModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance () : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService (retrofit: Retrofit) : CarBrandsAndModelsAPI {
        return retrofit.create(CarBrandsAndModelsAPI::class.java)
    }



}