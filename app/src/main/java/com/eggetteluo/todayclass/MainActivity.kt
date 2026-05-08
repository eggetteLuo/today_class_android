package com.eggetteluo.todayclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.ui.NavDisplay
import com.eggetteluo.todayclass.navigation.Navigator
import com.eggetteluo.todayclass.ui.theme.TodayClassAndroidTheme
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodayClassAndroidTheme {
                val navigator: Navigator = koinInject()
                NavDisplay(
                    backStack = navigator.backStack,
                    onBack = {
                        navigator.back()
                    },
                    entryProvider = koinEntryProvider()
                )
            }
        }
    }
}
