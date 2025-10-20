package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.accountbook.viewmodel.RecordViewModel
import java.util.Locale
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.accountbook.repository.DataState
import com.example.accountbook.model.Record
import com.example.accountbook.model.RecordType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordListScreen(
    onAddRecordClick: () -> Unit,
    onStatsClick:()->Unit,
    viewModel: RecordViewModel
) {
    val records = viewModel.records.collectAsStateWithLifecycle(initialValue = emptyList())
    val totalExpenses = viewModel.totalExpenses.collectAsStateWithLifecycle(initialValue = 0.0)
    val totalIncome = viewModel.totalIncome.collectAsStateWithLifecycle(initialValue = 0.0)
    val balance = viewModel.balance.collectAsStateWithLifecycle(initialValue = 0.0)

    // 收集操作状态 - 明确指定类型 指定泛型
    val operationState = viewModel.operationState.collectAsStateWithLifecycle<DataState<Unit>>()
    // 处理操作状态的副作用
    LaunchedEffect(operationState.value) {
        when (val state = operationState.value) {
            is DataState.Error -> {
                // 这里可以添加Snackbar显示错误信息
                println("操作失败: ${state.message}")
                // 未来可以集成Snackbar:
                // scaffoldState.snackbarHostState.showSnackbar(state.message)
            }
            is DataState.Success<*> -> {
                // 成功状态不需要处理
            }
            is DataState.Loading -> {
                // 加载状态不需要处理
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("记账本") },
                actions = {
                    // 统计按钮
                    IconButton(
                        onClick = onStatsClick,
                        enabled = operationState.value !is DataState.Loading
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = "统计"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // 根据加载状态决定FAB是否可用
            val isLoading = operationState.value is DataState.Loading
            IconButton (
                onClick = onAddRecordClick,
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Add, contentDescription = "添加记录")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 显示顶部加载指示器
            TopLoadingIndicator(
                isLoading = operationState.value is DataState.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            if (records.value.isEmpty()) {
//            if (true) {  // 临时强制显示空状态 测试状态
                EmptyRecordState(onAddRecordClick = onAddRecordClick)  // 使用新的空状态
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(records.value) { record ->
                        RecordItem(
                            record = record,
                            modifier = Modifier.padding(vertical = 4.dp),
                            onDelete = { viewModel.deleteRecord(record) },
                            isDeleting = operationState.value is DataState.Loading
                        )
                    }
                }
            }

            // 在底部显示统计信息
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text(
                    text = "总收入: ¥${String.format(Locale.getDefault(), "%.2f", totalIncome.value)} | " +
                            "总支出: ¥${String.format(Locale.getDefault(), "%.2f", totalExpenses.value)} | " +
                            "余额: ¥${String.format(Locale.getDefault(), "%.2f", balance.value)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

// RecordItem 组件需要更新以支持删除功能和加载状态 V0.31
@Composable
fun RecordItem(
    record: Record,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,  // 可选的删除功能
    isDeleting: Boolean = false      // 删除加载状态
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = record.category,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = record.note.ifEmpty { "无备注" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatRecordDate(record.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${if (record.type == RecordType.INCOME) "+" else "-"}¥${String.format(Locale.getDefault(), "%.2f", record.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = when (record.type) {
                        RecordType.INCOME -> MaterialTheme.colorScheme.primary
                        RecordType.EXPENSE -> MaterialTheme.colorScheme.error
                    }
                )

                // 删除按钮（如果提供了onDelete函数）
                onDelete?.let {
                    IconButton(
                        onClick = it,
                        enabled = !isDeleting
                    ) {
                        if (isDeleting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "删除记录",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

// 日期格式化函数（如果还没有的话）
private fun formatRecordDate(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val formatter = java.text.SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault())
    return formatter.format(date)
}

// 在 RecordListScreen.kt 中添加空状态组件
@Composable
fun EmptyRecordState(onAddRecordClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 图标
        Icon(
            imageVector = Icons.Outlined.ReceiptLong,
            contentDescription = "空状态",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 主标题
        Text(
            text = "还没有记账记录",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 副标题
        Text(
            text = "开始记录你的第一笔收支，\n掌握财务状况从今天开始",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 行动按钮
        Button(
            onClick = onAddRecordClick,
            modifier = Modifier.width(200.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = "开始记账",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("开始记账")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 次要提示
        Text(
            text = "点击右下角按钮也可添加记录",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline
        )
    }
}