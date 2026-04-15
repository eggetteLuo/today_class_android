package com.eggetteluo.todayclass.feature.home

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
fun HomeScreen(onOpenDetail: (String) -> Unit) {
    val listState = rememberLazyListState()
    val todayItems = List(30) { index -> "第 ${index + 1} 节课" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(todayItems) { course ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDetail(course) }
            ) {
                Text(
                    text = "$course（点击查看详情）",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
