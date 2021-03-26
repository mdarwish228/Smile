package com.darwish.smile

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class MyApplication : Application() {

    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = "smile_data")

    override fun onCreate() {
        super.onCreate()
    }

    fun getDataStoreSingleton(): DataStore<Preferences> {
        return dataStore
    }
}