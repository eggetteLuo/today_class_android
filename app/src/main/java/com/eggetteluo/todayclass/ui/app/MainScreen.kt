package com.eggetteluo.todayclass.ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eggetteluo.todayclass.feature.home.HomeScreen
import com.eggetteluo.todayclass.feature.setting.SettingScreen
import com.eggetteluo.todayclass.feature.week.WeekScreen
import com.eggetteluo.todayclass.navigation.BottomTab
import com.eggetteluo.todayclass.navigation.Navigator
import org.koin.compose.koinInject

@Composable
fun MainScreen() {
    val navigator: Navigator = koinInject()

    val tabs = listOf(BottomTab.Home, BottomTab.Week, BottomTab.Setting)

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = navigator.currentTab == tab,
                        onClick = {
                            navigator.switchTab(tab)
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(tab.title)
                        }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding)
        ) {
            when (navigator.currentTab) {
                BottomTab.Home -> {
                    HomeScreen()
                }

                BottomTab.Week -> {
                    WeekScreen()
                }

                BottomTab.Setting -> {
                    SettingScreen()
                }
            }
        }
    }
}
