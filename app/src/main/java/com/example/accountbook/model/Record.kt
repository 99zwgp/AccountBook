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
    val date: Long = System.currentTimeMillis(),
    // 新增：编辑相关字段
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val version: Int = 1
){
    // 新增：复制方法用于编辑
    fun copyWithUpdates(
        type: RecordType = this.type,
        amount: Double = this.amount,
        category: String = this.category,
        note: String = this.note,
        date: Long = this.date
    ): Record {
        return this.copy(
            type = type,
            amount = amount,
            category = category,
            note = note,
            date = date
        )
    }
}

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


