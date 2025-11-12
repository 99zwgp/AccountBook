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

    // 当前登录用户ID，默认为空字符串
    private var _currentUserId = ""
    
    // 用户特定的记录流
    private val _records = MutableStateFlow<List<Record>>(emptyList())
    val records: StateFlow<List<Record>> = _records

    // 根据当前用户ID更新记录流
    private fun updateRecordsForCurrentUser() {
        println("DEBUG: 开始更新用户 ${_currentUserId} 的记录流")
        viewModelScope.launch {
            try {
                recordRepository.getRecordsByUserId(_currentUserId).collect { records ->
                    println("DEBUG: 接收到用户 ${_currentUserId} 的记录，数量: ${records.size}")
                    _records.value = records
                    _recordsCache.clear()
                    _recordsCache.addAll(records)
                    println("DEBUG: 记录流更新完成，当前用户: ${_currentUserId}")
                }
            } catch (e: Exception) {
                println("DEBUG: 记录流更新失败: ${e.message}")
            }
        }
    }

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

    // 设置当前用户ID并更新记录
    fun setCurrentUserId(userId: String) {
        println("DEBUG: 设置当前用户ID: $userId")
        // 如果用户ID发生变化，先清除原有数据
        if (_currentUserId != userId) {
            println("DEBUG: 用户ID发生变化，从 $_currentUserId 变为 $userId")
            _records.value = emptyList()
            _recordsCache.clear()
            _currentUserId = userId
            // 立即强制更新记录流
            updateRecordsForCurrentUser()
        }
    }

    // 清除当前用户ID（登出时使用）
    fun clearCurrentUserId() {
        println("DEBUG: 清除当前用户ID")
        _currentUserId = ""
        _records.value = emptyList()
        _recordsCache.clear()
    }

    // 获取当前用户ID
    fun getCurrentUserId(): String? {
        return if (_currentUserId.isNotEmpty()) _currentUserId else null
    }

    init {
        // 初始化时如果用户ID为空，显示空列表
        if (_currentUserId.isEmpty()) {
            _records.value = emptyList()
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
        println("DEBUG: 添加记录，用户ID: ${record.userId}, 当前用户: $_currentUserId")
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
        return recordRepository.getRecordById(id, _currentUserId)
    }
    // 添加同步版本的方法供Composable使用
    fun getRecordByIdSync(id: String): Record? {
        return _recordsCache.find { it.id == id }
    }
}