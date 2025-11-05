// ui/components/FormState.kt
package com.example.accountbook.ui.components

import com.example.accountbook.model.RecordType
import java.util.Date

data class AddRecordFormState(
    val amount: String = "",
    val amountError: String? = null,
    val category: String = "",
    val categoryError: String? = null,
    val note: String = "",
    val type: RecordType = RecordType.EXPENSE,
    val date: Long = System.currentTimeMillis()   // 添加 date 字段
) {
    // 在类内部添加验证方法 V0.4
    fun validate(): ValidationResult {
        return when {
            amount.isBlank() -> ValidationResult(false, "金额不能为空")
            amount.toDoubleOrNull() == null -> ValidationResult(false, "请输入有效的数字")
            amount.toDouble() <= 0 -> ValidationResult(false, "金额必须大于0")
            amount.toDouble() > 1000000 -> ValidationResult(false, "金额不能超过100万")
            category.isBlank() -> ValidationResult(false, "请选择分类")
            else -> ValidationResult(true)
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String = ""
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