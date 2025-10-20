package com.example.accountbook.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.accountbook.model.StatisticsData
import com.example.accountbook.viewmodel.RecordViewModel
import com.example.accountbook.ui.components.PieChartSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBackClick: () -> Unit,
    viewModel: RecordViewModel
) {
    // 修复：为collectAsState提供初始值
    // 修复：提供完整的StatisticsData参数，包括categoryExpense
    val statistics by viewModel.statistics.collectAsState(
        initial = StatisticsData(
            totalIncome = 0.0,
            totalExpense = 0.0,
            balance = 0.0,
            categoryExpense = emptyMap()  // 添加这个参数
        )
    )
    val records by viewModel.records.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("收支统计") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "返回")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (records.isEmpty()) {
            EmptyStatisticsState(innerPadding)
        } else {
            StatisticsContent(statistics, innerPadding)
        }
    }
}


@Composable
fun EmptyStatisticsState(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("暂无数据，请先添加记录")
    }
}

@Composable
fun StatisticsContent(statistics: StatisticsData, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier.padding(padding)
    ) {
        item {
            KeyMetricsRow(statistics)
        }
        // 新增：分类支出明细
        item {
            CategoryExpenseSection(statistics.categoryExpense)
        }
        // 新增：饼图部分
        item {
            PieChartSection(statistics)  // 这里调用组件
        }
    }
}

@Composable
fun KeyMetricsRow(statistics: StatisticsData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MetricCard(
            title = "总收入",
            amount = statistics.totalIncome,
            color = Color(0xFF4CAF50),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        MetricCard(
            title = "总支出",
            amount = statistics.totalExpense,
            color = Color(0xFFF44336),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        MetricCard(
            title = "余额",
            amount = statistics.balance,
            color = Color(0xFF2196F3),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun MetricCard(
    title: String,
    amount: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "¥${"%.2f".format(amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 新增：分类支出明细组件
@Composable
fun CategoryExpenseSection(categoryExpense: Map<String, Double>) {
    if (categoryExpense.isNotEmpty()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "分类支出",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp),
                fontWeight = FontWeight.Bold
            )

            // 按金额降序排序显示
            val sortedCategories = categoryExpense.entries.sortedByDescending { it.value }

            sortedCategories.forEach { (category, amount) ->
                CategoryExpenseItem(
                    category = category,
                    amount = amount,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryExpenseItem(
    category: String,
    amount: Double,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "¥${"%.2f".format(amount)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}