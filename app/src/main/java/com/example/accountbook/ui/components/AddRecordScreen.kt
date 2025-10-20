package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecordScreen(
    onRecordSaved: () -> Unit,
    onCancel: () -> Unit,       // 新增取消回调
    viewModel: RecordViewModel = viewModel()
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(RecordType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf("") }

    val categories = remember(selectedType) {
        when (selectedType) {
            RecordType.INCOME -> RecordCategories.INCOME_CATEGORIES
            RecordType.EXPENSE -> RecordCategories.EXPENSE_CATEGORIES
        }
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 金额输入
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("金额") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            // 类型选择
            Text("类型", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RecordType.entries.forEach { type ->
                    val isSelected = type == selectedType
                    val backgroundColor =
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant

                    TextButton(
                        onClick = {
                            selectedType = type
                            selectedCategory = ""
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = backgroundColor,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(
                            text = when (type) {
                                RecordType.INCOME -> "收入"
                                RecordType.EXPENSE -> "支出"
                            }
                        )
                    }
                }
            }

            // 分类选择
            Text("分类", style = MaterialTheme.typography.titleMedium)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    val backgroundColor =
                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant

                    Button(
                        onClick = { selectedCategory = category },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = backgroundColor,
                            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            // 备注输入
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 保存按钮
            Button(
                onClick = {
                    if (amount.isBlank() || selectedCategory.isBlank()) {
                        return@Button
                    }
                    val amountValue = amount.toDoubleOrNull() ?: return@Button

                    val newRecord = Record(
                        type = selectedType,
                        amount = amountValue,
                        category = selectedCategory,
                        note = note
                    )

                    viewModel.addRecord(newRecord)
                    onRecordSaved()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCategory.isNotBlank() && amount.isNotBlank()
            ) {
                Text("保存")
            }
        }
    }
}