package com.eggetteluo.todayclass.feature.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eggetteluo.todayclass.data.datastore.ThemePreferencesManager
import com.eggetteluo.todayclass.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingViewModel(
    private val themePreferencesManager: ThemePreferencesManager
) : ViewModel() {

    val currentTheme: StateFlow<AppThemeMode> = themePreferencesManager.themeModeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppThemeMode.FOLLOW_SYSTEM
        )

    /**
     * 更新应用的主题模式
     */
    fun selectThemeMode(mode: AppThemeMode) {
        viewModelScope.launch {
            themePreferencesManager.setThemeMode(mode)
        }
    }
}