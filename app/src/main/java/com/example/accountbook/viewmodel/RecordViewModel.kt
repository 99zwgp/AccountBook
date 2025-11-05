package com.example.accountbook.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountbook.model.Record
import com.example.accountbook.model.RecordType
import com.example.accountbook.model.StatisticsData
import com.example.accountbook.repository.RecordRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import com.example.accountbook.repository.DataState
import kotlinx.coroutines.flow.MutableStateFlow

class RecordViewModel(private val recordRepository: RecordRepository) : ViewModel() {

    val records = recordRepository.getAllRecords()

    val totalExpenses = records.map { records ->
        records.filter { it.type == RecordType.EXPENSE }.sumOf { it.amount }
    }

    val totalIncome = records.map { records ->
        records.filter { it.type == RecordType.INCOME }.sumOf { it.amount }
    }

    val balance = records.map { records ->
        records.sumOf { record ->
            when (record.type) {
                RecordType.INCOME -> record.amount
                RecordType.EXPENSE -> -record.amount
            }
        }
    }

    // 新增：统计数据的StateFlow V0.3
    val statistics = records.map { records ->
        calculateStatistics(records)
    }

    // 新增：暴露加载状态
    val operationState: StateFlow<DataState<Unit>> = recordRepository.operationState

    // 新增：编辑状态跟踪 V0.4
    private val _editingRecord = MutableStateFlow<Record?>(null)
    val editingRecord: StateFlow<Record?> = _editingRecord

    // 缓存当前记录列表的状态 - 用于同步获取记录
    private val _recordsCache = mutableStateListOf<Record>()
    val recordsCache: List<Record> get() = _recordsCache

    init {
        // 监听记录变化并更新缓存 - 修复：使用正确的 recordRepository
        viewModelScope.launch {
            recordRepository.getAllRecords().collect { records ->
                _recordsCache.clear()
                _recordsCache.addAll(records)
            }
        }
    }

    // 新增统计计算方法 V0.3
    // 私有的统计计算方法
    private fun calculateStatistics(records: List<Record>): StatisticsData {
        val totalIncome = records.filter { it.type == RecordType.INCOME }
            .sumOf { it.amount }
        val totalExpense = records.filter { it.type == RecordType.EXPENSE }
            .sumOf { it.amount }
        val balance = totalIncome - totalExpense

        val categoryExpense = records
            .filter { it.type == RecordType.EXPENSE }
            .groupBy { it.category }
            .mapValues { (_, records) -> records.sumOf { it.amount } }

        return StatisticsData(totalIncome, totalExpense, balance, categoryExpense)
    }

    fun addRecord(record: Record) {
        viewModelScope.launch {
            recordRepository.addRecord(record)
        }
    }

    fun updateRecord(record: Record) {
        viewModelScope.launch {
            recordRepository.updateRecord(record)
        }
    }

    fun deleteRecord(record: Record) {
        viewModelScope.launch {
            recordRepository.deleteRecord(record)
        }
    }
    // 新增：清除操作状态
    fun clearOperationState() {
        viewModelScope.launch {
            recordRepository.clearOperationState()
        }
    }

    // 新增：编辑相关方法V0.4
    suspend fun getRecordById(id: String): Record? {
        return recordRepository.getRecordById(id)
    }
    // 添加同步版本的方法供Composable使用
    fun getRecordByIdSync(id: String): Record? {
        return _recordsCache.find { it.id == id }
    }
}