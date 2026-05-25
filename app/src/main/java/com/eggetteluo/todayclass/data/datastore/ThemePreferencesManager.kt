package com.eggetteluo.todayclass.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eggetteluo.todayclass.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class ThemePreferencesManager(private val context: Context) {

    private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")

    val themeModeFlow: Flow<AppThemeMode> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { preferences ->
            val themeName = preferences[THEME_MODE_KEY]
            AppThemeMode.entries.find { it.name == themeName } ?: AppThemeMode.FOLLOW_SYSTEM
        }

    suspend fun setThemeMode(mode: AppThemeMode) {
        context.dataStore.edit { it[THEME_MODE_KEY] = mode.name }
    }
}