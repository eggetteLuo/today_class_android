package com.eggetteluo.todayclass.ui.root

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.staticCompositionLocalOf

// 定义全局的 SnackbarHostState 提供者
val LocalSnackbarHostState = staticCompositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided. Have you wrapped your app with CompositionLocalProvider?")
}