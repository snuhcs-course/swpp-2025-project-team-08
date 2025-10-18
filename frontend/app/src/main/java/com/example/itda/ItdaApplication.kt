package com.example.itda

import android.app.Application
import com.example.itda.data.source.local.PrefDataSource
import com.example.itda.data.source.remote.RetrofitInstance
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ItdaApplication : Application() {
    @Inject
    lateinit var prefDataSource: PrefDataSource

    override fun onCreate() {
        super.onCreate()
        RetrofitInstance.init(prefDataSource)
    }
}