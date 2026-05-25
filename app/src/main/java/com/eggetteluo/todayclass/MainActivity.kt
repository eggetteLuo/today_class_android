package com.eggetteluo.todayclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eggetteluo.todayclass.data.datastore.ThemePreferencesManager
import com.eggetteluo.todayclass.ui.root.RootScreen
import com.eggetteluo.todayclass.ui.theme.AppThemeMode
import com.eggetteluo.todayclass.ui.theme.TodayClassAndroidTheme
import org.koin.android.ext.android.inject
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {

    private val themePreferencesManager: ThemePreferencesManager by inject()

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themePreferencesManager.themeModeFlow.collectAsStateWithLifecycle(
                initialValue = AppThemeMode.FOLLOW_SYSTEM
            )
            TodayClassAndroidTheme(themeMode = themeMode) {
                RootScreen()
            }
        }
    }
}