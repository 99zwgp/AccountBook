package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.accountbook.viewmodel.RecordViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    onAddRecordClick: () -> Unit,
    onStatsClick:()->Unit,
    viewModel: RecordViewModel
) {
    val records = viewModel.records.collectAsStateWithLifecycle(initialValue = emptyList())
    val totalExpenses = viewModel.totalExpenses.collectAsStateWithLifecycle(initialValue = 0.0)
    val totalIncome = viewModel.totalIncome.collectAsStateWithLifecycle(initialValue = 0.0)
    val balance = viewModel.balance.collectAsStateWithLifecycle(initialValue = 0.0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("记账本") },
                actions = {
                    // 统计按钮
                    IconButton(onClick = onStatsClick) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = "统计"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddRecordClick) {
                Icon(Icons.Default.Add, contentDescription = "添加记录")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (records.value.isEmpty()) {
                Text(
                    text = "暂无记账记录\n点击右下角按钮添加",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(records.value) { record ->
                        RecordItem(
                            record = record,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }

            // 在底部显示统计信息
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = "总收入: ¥${String.format(Locale.getDefault(), "%.2f", totalIncome.value)} | " +
                            "总支出: ¥${String.format(Locale.getDefault(), "%.2f", totalExpenses.value)} | " +
                            "余额: ¥${String.format(Locale.getDefault(), "%.2f", balance.value)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}