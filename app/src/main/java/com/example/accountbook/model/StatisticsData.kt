// model/StatisticsData.kt
package com.example.accountbook.model

data class StatisticsData(
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double,
    val categoryExpense: Map<String, Double> // 分类:金额
)