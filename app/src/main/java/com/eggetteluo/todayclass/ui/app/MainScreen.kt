package com.eggetteluo.todayclass.ui.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.eggetteluo.todayclass.feature.home.HomeScreen
import com.eggetteluo.todayclass.feature.setting.SettingScreen
import com.eggetteluo.todayclass.feature.week.WeekScreen
import com.eggetteluo.todayclass.navigation.BottomTab
import com.eggetteluo.todayclass.navigation.Navigator
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navigator: Navigator = koinInject()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    val tabs = listOf(BottomTab.Home, BottomTab.Week, BottomTab.Setting)

    Scaffold(
        topBar = {
            MediumTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        text = "今日课表",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
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
