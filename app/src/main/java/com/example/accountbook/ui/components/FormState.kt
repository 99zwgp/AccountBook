// ui/components/FormState.kt
package com.example.accountbook.ui.components

import com.example.accountbook.model.RecordType

data class AddRecordFormState(
    val amount: String = "",
    val amountError: String? = null,
    val category: String = "",
    val categoryError: String? = null,
    val note: String = "",
    val type: RecordType = RecordType.EXPENSE
)

// 在 FormState.kt 中添加验证函数
fun validateAmount(amount: String): String? {
    return when {
        amount.isBlank() -> "金额不能为空"
        amount.toDoubleOrNull() == null -> "请输入有效的数字"
        amount.toDouble() <= 0 -> "金额必须大于0"
        amount.toDouble() > 1000000 -> "金额不能超过100万"
        else -> null
    }
}

fun validateCategory(category: String): String? {
    return if (category.isBlank()) "请选择分类" else null
}

fun validateForm(state: AddRecordFormState): Boolean {
    return state.amountError == null &&
            state.categoryError == null &&
            state.amount.isNotBlank() &&
            state.category.isNotBlank()
}