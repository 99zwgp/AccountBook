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
import kotlinx.coroutines.launch

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

    // 实时金额验证
    LaunchedEffect(formState.amount) {
        formState = formState.copy(
            amountError = validateAmount(formState.amount)
        )
    }

    // 实时分类验证
    LaunchedEffect(formState.category) {
        formState = formState.copy(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 金额输入
            Column {
                OutlinedTextField(
                    value = formState.amount,
                    onValueChange = { newAmount ->
                        formState = formState.copy(amount = newAmount)
                    },
                    label = { Text("金额") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.amountError != null,
                    trailingIcon = {
                        Text("¥", style = MaterialTheme.typography.bodyLarge)
                    }
                )
                if (formState.amountError != null) {
                    Text(
                        text = formState.amountError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }

            // 类型选择
            Column {
                Text("类型", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecordType.entries.forEach { type ->
                        val isSelected = type == formState.type
                        val backgroundColor =
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant

                        TextButton(
                            onClick = {
                                formState = formState.copy(
                                    type = type,
                                    category = ""
                                )
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = backgroundColor,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(text = type.displayName)
                        }
                    }
                }
            }

            // 分类选择
            Column {
                Text("分类", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category == formState.category
                        val backgroundColor =
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant

                        Button(
                            onClick = {
                                formState = formState.copy(category = category)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = backgroundColor,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                else MaterialTheme.colorScheme.onSurfaceVariant
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
                if (formState.categoryError != null) {
                    Text(
                        text = formState.categoryError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // 备注输入
            OutlinedTextField(
                value = formState.note,
                onValueChange = { newNote ->
                    formState = formState.copy(note = newNote)
                },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 保存按钮
            Button(
                onClick = {
                    scope.launch {
                        if (validateForm(formState)) {
                            val amountValue = formState.amount.toDoubleOrNull() ?: return@launch

                            val newRecord = Record(
                                type = formState.type,
                                amount = amountValue,
                                category = formState.category,
                                note = formState.note
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
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = validateForm(formState)
            ) {
                Text("保存记录", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}