package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accountbook.model.Record
import com.example.accountbook.model.RecordCategories
import com.example.accountbook.model.RecordType
import com.example.accountbook.viewmodel.RecordViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    onRecordSaved: () -> Unit,
    onCancel: () -> Unit,
    viewModel: RecordViewModel = viewModel()
) {
    var formState by remember { mutableStateOf(AddRecordFormState()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val categories = remember(formState.type) {
        when (formState.type) {
            RecordType.INCOME -> RecordCategories.INCOME_CATEGORIES
            RecordType.EXPENSE -> RecordCategories.EXPENSE_CATEGORIES
        }
    }

    // 实时验证
    LaunchedEffect(formState.amount,formState.category) {
        formState = formState.copy(
            amountError = validateAmount(formState.amount),
            categoryError = validateCategory(formState.category)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("添加记录") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 金额输入卡片
            item {
                AmountInputCard(
                    amount = formState.amount,
                    amountError = formState.amountError,
                    onAmountChange = { newAmount ->
                        formState = formState.copy(amount = newAmount)
                    }
                )
            }

            // 2. 类型选择卡片
            item {
                TypeSelectionCard(
                    selectedType = formState.type,
                    onTypeSelected = { newType ->
                        formState = formState.copy(
                            type = newType,
                            category = "" // 切换类型时清空分类
                        )
                    }
                )
            }

            // 3. 分类选择卡片 - 修复核心问题
            item {
                CategorySelectionCard(
                    selectedCategory = formState.category,
                    categories = categories,
                    categoryError = formState.categoryError,
                    onCategorySelected = { newCategory ->
                        formState = formState.copy(category = newCategory)
                    }
                )
            }

            // 4. 备注输入卡片
            item {
                NoteInputCard(
                    note = formState.note,
                    onNoteChange = { newNote ->
                        formState = formState.copy(note = newNote)
                    }
                )
            }

            // 5. 时间选择卡片 - 增强功能
            item {
                EnhancedDatePickerCard(
                    selectedDate = formState.date,
                    onDateSelected = { newDate ->
                        formState = formState.copy(date = newDate)
                    }
                )
            }
            // 6. 保存按钮
            item {
                SaveButtonCard(
                    isEnabled = validateForm(formState),
                    onSave = {
                        scope.launch {
                            if (validateForm(formState)) {
                                val amountValue = formState.amount.toDoubleOrNull() ?: return@launch

                                val currentUser = viewModel.getCurrentUserId()
                                val newRecord = Record(
                                    type = formState.type,
                                    amount = amountValue,
                                    category = formState.category,
                                    note = formState.note,
                                    date = formState.date, // 包含时间戳
                                    userId = currentUser ?: "" // 临时处理，需要真实的用户ID
                                )

                                viewModel.addRecord(newRecord)

                                snackbarHostState.showSnackbar(
                                    message = "成功添加${formState.category}记录",
                                    duration = SnackbarDuration.Short
                                )

                                kotlinx.coroutines.delay(500)
                                onRecordSaved()
                            } else {
                                snackbarHostState.showSnackbar(
                                    message = "请检查输入内容",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
// 金额输入卡片
@Composable
private fun AmountInputCard(
    amount: String,
    amountError: String?,
    onAmountChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "金额",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text("输入金额") },
                placeholder = { Text("0.00") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                isError = amountError != null,
                leadingIcon = {
                    Icon(Icons.Default.AttachMoney, contentDescription = "金额")
                },
                trailingIcon = {
                    Text("¥", style = MaterialTheme.typography.bodyLarge)
                }
            )
            if (amountError != null) {
                Text(
                    text = amountError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

// 类型选择卡片
@Composable
private fun TypeSelectionCard(
    selectedType: RecordType,
    onTypeSelected: (RecordType) -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "类型",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RecordType.entries.forEach { type ->
                    val isSelected = type == selectedType
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTypeSelected(type) },
                        label = { Text(type.displayName) },
                        leadingIcon = if (isSelected) {
                            { Icon(Icons.Default.Check, null) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// 分类选择卡片 - 修复核心问题
@Composable
private fun CategorySelectionCard(
    selectedCategory: String,
    categories: List<String>,
    categoryError: String?,
    onCategorySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "分类",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            // 分类网格 - 自适应高度
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 240.dp) // 自适应高度
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(category) },
                        label = {
                            Text(
                                category,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (categoryError != null) {
                Text(
                    text = categoryError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

// 备注输入卡片
@Composable
private fun NoteInputCard(
    note: String,
    onNoteChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "备注",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                placeholder = { Text("添加备注（可选）") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )
        }
    }
}

// 增强的时间选择卡片
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedDatePickerCard(
    selectedDate: Long,
    onDateSelected: (Long) -> Unit
) {

    Card(
        modifier = Modifier.padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "时间",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 直接使用现有的DatePickerField组件
            DatePickerField(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// 保存按钮卡片
@Composable
private fun SaveButtonCard(
    isEnabled: Boolean,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.padding(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = isEnabled,
            shape = MaterialTheme.shapes.medium
        ) {
            Text("保存记录", style = MaterialTheme.typography.titleMedium)
        }
    }
}

private fun calculateQuickDate(option: String): Long {
    // 实现快捷时间计算逻辑
    return System.currentTimeMillis() // 临时实现
}