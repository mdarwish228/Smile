package com.darwish.smile

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.runBlocking

class SmileCount : AppCompatActivity() {

    private val smileCounter = intPreferencesKey("smile_counter")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smile_count)

        val mApplication = applicationContext as MyApplication

        runBlocking {
            incrementCounter(mApplication.getDataStoreSingleton())
        }
    }

    private suspend fun incrementCounter(dataStore: DataStore<Preferences>) {
        dataStore.edit { data ->
            val currentCounterValue = data[smileCounter] ?: 0

            val newSmileCount = currentCounterValue + 1
            data[smileCounter] = newSmileCount

            val counterTextView = findViewById<TextView>(R.id.smile_count);
            counterTextView.text = newSmileCount.toString()
        }
    }
}