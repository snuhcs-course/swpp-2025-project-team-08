package com.example.itda.di

import com.example.itda.data.source.remote.AuthAPI
import com.example.itda.data.source.remote.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthAPI(): AuthAPI {
        return RetrofitInstance.authAPI
    }
}
