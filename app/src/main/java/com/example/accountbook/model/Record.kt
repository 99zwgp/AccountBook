package com.example.accountbook.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "records")
data class Record(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: RecordType,
    val amount: Double,
    val category: String,
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)

// 修改这里：使用枚举构造函数参数
enum class RecordType(val displayName: String) {
    INCOME("收入"),
    EXPENSE("支出")
}

// 添加扩展属性
//val RecordType.displayName: String
//    get() = when (this) {
//        RecordType.INCOME -> "收入"
//        RecordType.EXPENSE -> "支出"
//    }

object RecordCategories {
    val EXPENSE_CATEGORIES = listOf("餐饮", "交通", "购物", "娱乐", "医疗", "教育", "住房", "其他")
    val INCOME_CATEGORIES = listOf("工资", "奖金", "投资", "兼职", "其他")
}