package com.eggetteluo.todayclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eggetteluo.todayclass.ui.app.TodayClassApp
import com.eggetteluo.todayclass.ui.theme.TodayClassAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodayClassAndroidTheme {
                TodayClassApp()
            }
        }
    }
}
