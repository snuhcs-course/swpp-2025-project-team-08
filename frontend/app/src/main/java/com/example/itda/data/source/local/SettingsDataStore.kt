package com.example.itda.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.itda.ui.profile.SettingsViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val FONT_SIZE = stringPreferencesKey("font_size")
    }

    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.DARK_MODE] ?: false
    }

    val fontSizeFlow: Flow<SettingsViewModel.FontSize> = context.dataStore.data.map { preferences ->
        val fontSizeString = preferences[PreferencesKeys.FONT_SIZE] ?: SettingsViewModel.FontSize.MEDIUM.name
        try {
            SettingsViewModel.FontSize.valueOf(fontSizeString)
        } catch (e: IllegalArgumentException) {
            SettingsViewModel.FontSize.MEDIUM
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    suspend fun setFontSize(fontSize: SettingsViewModel.FontSize) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FONT_SIZE] = fontSize.name
        }
    }
}