package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import com.example.accountbook.repository.DataState
import com.example.accountbook.viewmodel.RecordViewModel
import kotlinx.coroutines.launch
import com.example.accountbook.model.Record
import kotlinx.coroutines.flow.collect


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecordScreen(
    recordId: String,
    onRecordUpdated: () -> Unit,
    onCancel: () -> Unit,
    viewModel: RecordViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var recordToEdit by remember { mutableStateOf<Record?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var hasUpdated by remember { mutableStateOf(false) }  // 新增：跟踪是否已更新

    LaunchedEffect(recordId) {
        if (recordId.isNotEmpty()) {
            isLoading = true
            loadError = null
            try {
                println("DEBUG: 编辑页面开始加载记录，ID: $recordId")
                // 使用同步方法而不是异步方法
                recordToEdit = viewModel.getRecordByIdSync(recordId)
                println("DEBUG: 加载到的记录: $recordToEdit")
                if (recordToEdit == null) {
                    loadError = "未找到记录"
                }
            } catch (e: Exception) {
                loadError = "加载记录失败: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    val operationState by viewModel.operationState.collectAsState()

    // 修复：只在真正更新操作成功后返回
    LaunchedEffect(operationState) {
        val currentState = operationState
        when (currentState) {
            is DataState.Success<*> -> {
                // 只有在确实执行了更新操作后才返回
                if (hasUpdated) {
                    println("DEBUG: 记录更新成功，返回列表")
                    onRecordUpdated()
                }
            }
            is DataState.Error -> {
                println("DEBUG: 保存失败: ${currentState.message}")
                // 重置更新标志，允许重试
                hasUpdated = false
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑记录") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("加载中...")
                        }
                    }
                }
                loadError != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("加载失败", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(loadError!!, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onCancel) {
                                Text("返回")
                            }
                        }
                    }
                }
                recordToEdit == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("记录不存在", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onCancel) {
                                Text("返回")
                            }
                        }
                    }
                }
                else -> {
                    var formState by remember {
                        mutableStateOf(
                            AddRecordFormState(
                                amount = recordToEdit!!.amount.toString(),
                                category = recordToEdit!!.category,
                                note = recordToEdit!!.note,
                                type = recordToEdit!!.type,
                                date = recordToEdit!!.date
                            )
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RecordTypeSelector(
                            selectedType = formState.type,
                            onTypeSelected = { formState = formState.copy(type = it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        AmountInput(
                            amount = formState.amount,
                            onAmountChange = { formState = formState.copy(amount = it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        CategoryDropdown(
                            selectedCategory = formState.category,
                            onCategorySelected = { formState = formState.copy(category = it) },
                            recordType = formState.type,
                            modifier = Modifier.fillMaxWidth()
                        )
                        NoteInput(
                            note = formState.note,
                            onNoteChange = { formState = formState.copy(note = it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DatePickerField(
                            selectedDate = formState.date,
                            onDateSelected = { formState = formState.copy(date = it) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        val isSaving = operationState is DataState.Loading
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val validationResult = formState.validate()
                                    if (validationResult.isValid) {
                                        println("DEBUG: 开始更新记录")
                                        hasUpdated = true  // 标记已执行更新操作
                                        val updatedRecord = recordToEdit!!.copyWithUpdates(
                                            type = formState.type,
                                            amount = formState.amount.toDouble(),
                                            category = formState.category,
                                            note = formState.note,
                                            date = formState.date
                                        )
                                        viewModel.updateRecord(updatedRecord)
                                    } else {
                                        println("DEBUG: 表单验证失败: ${validationResult.errorMessage}")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("更新记录")
                            }
                        }
                    }
                }
            }
        }
    }
}