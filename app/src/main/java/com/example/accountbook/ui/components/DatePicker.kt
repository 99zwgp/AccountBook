// DatePicker.kt
package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val userSelectedQuickOption = remember { mutableStateOf<String?>(null) }
    val initialDate = if (selectedDate == 0L) System.currentTimeMillis() else selectedDate
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

    // 快捷时间选项 - 修复：使用Pair而不是函数引用
    val quickTimeOptions = remember {
        listOf(
            "1小时前" to getOneHourAgo(),
            "今天早上" to getTodayMorning(),
            "昨天" to getYesterday(),
            "前天" to getDayBeforeYesterday()
        )
    }

    Column(modifier = modifier) {
        // 日期显示区域
        OutlinedTextField(
            value = formatDateTime(selectedDate),
            onValueChange = { }, // 只读
            readOnly = true,
            label = { Text("选择时间") },trailingIcon = {
                IconButton(onClick = {
                    showDatePicker = true
                    userSelectedQuickOption.value = null // 打开日期选择器时清除快捷选项
                }) {
                    Icon(Icons.Default.DateRange, "选择时间")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 快捷时间选项
        Text(
            text = "快捷选择:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            quickTimeOptions.forEach { (label, timestamp) ->
                // 修复高亮逻辑：只有用户明确选择的选项才高亮
                val isSelected = userSelectedQuickOption.value == label
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onDateSelected(timestamp)
                        userSelectedQuickOption.value = label // 记录用户选择
                    },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        // 日期选择器对话框
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {
                    showDatePicker = false
                    userSelectedQuickOption.value = null // 关闭时清除快捷选项
                    },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { dateMillis ->
                                // 保持原有时间，只更新日期部分
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = selectedDate
                                }
                                val selectedCalendar = Calendar.getInstance().apply {
                                    timeInMillis = dateMillis
                                }
                                selectedCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                                selectedCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                                selectedCalendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND))

                                onDateSelected(selectedCalendar.timeInMillis)

                                userSelectedQuickOption.value = null // 选择日期后清除快
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                        userSelectedQuickOption.value = null
                    }) {
                        Text("取消")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}


// 时间格式化函数
private fun formatDateTime(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA)
    return formatter.format(date)
}
private fun getOneHourAgo(): Long {
    return System.currentTimeMillis() - 3600000
}

// 获取今天早上8点
private fun getTodayMorning(): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

// 获取昨天同一时间
private fun getYesterday(): Long {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -1)
        // 保持与当前时间相同的小时和分钟
        val currentCalendar = Calendar.getInstance()
        set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
        set(Calendar.SECOND, currentCalendar.get(Calendar.SECOND))
    }
    return calendar.timeInMillis
}

// 获取前天同一时间
private fun getDayBeforeYesterday(): Long {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -2)
        // 保持与当前时间相同的小时和分钟
        val currentCalendar = Calendar.getInstance()
        set(Calendar.HOUR_OF_DAY, currentCalendar.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, currentCalendar.get(Calendar.MINUTE))
        set(Calendar.SECOND, currentCalendar.get(Calendar.SECOND))
    }
    return calendar.timeInMillis
}

