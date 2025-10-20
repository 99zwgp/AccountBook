// ui/components/PieChartComponent.kt
package com.example.accountbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.accountbook.model.StatisticsData

@Composable
fun PieChartSection(statistics: StatisticsData) {
    if (statistics.categoryExpense.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                "支出分类占比",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp),
                fontWeight = FontWeight.Bold
            )

            // 简单的文本饼图替代方案（避免复杂依赖）
            SimpleCategoryChart(categoryExpense = statistics.categoryExpense)
        }
    }
}

@Composable
fun SimpleCategoryChart(categoryExpense: Map<String, Double>) {
    val totalExpense = categoryExpense.values.sum()
    val sortedCategories = categoryExpense.entries.sortedByDescending { it.value }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sortedCategories.forEach { (category, amount) ->
            val percentage = if (totalExpense > 0) (amount / totalExpense * 100) else 0.0

            CategoryProgressItem(
                category = category,
                amount = amount,
                percentage = percentage,
                color = getCategoryColor(category)
            )
        }
    }
}

@Composable
fun CategoryProgressItem(
    category: String,
    amount: Double,
    percentage: Double,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "¥${"%.2f".format(amount)} (${"%.1f".format(percentage)}%)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 简单的进度条显示占比
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.small
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percentage / 100f).toFloat())
                    .height(8.dp)
                    .background(
                        color = color,
                        shape = MaterialTheme.shapes.small
                    )
            )
        }
    }
}

// 为不同分类分配颜色
fun getCategoryColor(category: String): Color {
    return when (category) {
        "住房" -> Color(0xFF4CAF50)    // 绿色
        "餐饮" -> Color(0xFF2196F3)    // 蓝色
        "购物" -> Color(0xFFFF9800)    // 橙色
        "交通" -> Color(0xFF9C27B0)    // 紫色
        "娱乐" -> Color(0xFFF44336)    // 红色
        "医疗" -> Color(0xFF607D8B)    // 灰色
        "教育" -> Color(0xFF009688)    // 青色
        "工资" -> Color(0xFF4CAF50)    // 绿色
        "奖金" -> Color(0xFF2196F3)    // 蓝色
        "投资" -> Color(0xFFFF9800)    // 橙色
        "兼职" -> Color(0xFF9C27B0)    // 紫色
        else -> Color(0xFF757575)      // 默认灰色
    }
}