package com.example.itda.di

import com.example.itda.data.repository.AuthRepository
import com.example.itda.data.repository.AuthRepositoryImpl
import com.example.itda.data.repository.ProgramRepository
import com.example.itda.data.repository.ProgramRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProgramRepository(
        impl: ProgramRepositoryImpl
    ): ProgramRepository
}