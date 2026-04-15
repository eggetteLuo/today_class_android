package com.eggetteluo.todayclass.feature.week

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeekScreen(onOpenDetail: (String) -> Unit) {
    val listState = rememberLazyListState()
    val weekItems = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(weekItems) { day ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDetail(day) }
            ) {
                Text(
                    text = "$day: 点击后可进入当天详情",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
