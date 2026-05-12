package com.eggetteluo.todayclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eggetteluo.todayclass.ui.root.RootScreen
import com.eggetteluo.todayclass.ui.theme.TodayClassAndroidTheme
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity() {
    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodayClassAndroidTheme {
                RootScreen()
            }
        }
    }
}
