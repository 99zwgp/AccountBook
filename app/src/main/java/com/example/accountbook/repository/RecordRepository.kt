package com.example.accountbook.repository  // 确保包名正确

import com.example.accountbook.model.AppDatabase
import com.example.accountbook.model.Record
import com.example.accountbook.model.RecordDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


// 定义数据操作状态（在类外部，这样其他文件也可以使用）
sealed class DataState<out T> {
    object Loading : DataState<Nothing>()
    data class Success<T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}
class RecordRepository(private val appDatabase: AppDatabase) {

    // 获取DAO实例
    private val recordDao: RecordDao = appDatabase.recordDao()

    // 操作状态管理 - 使用密封类替代简单的Boolean
    private val _operationState = MutableStateFlow<DataState<Unit>>(DataState.Success(Unit))
    val operationState: StateFlow<DataState<Unit>> = _operationState

    // 统计加载状态
//    private val _statsState = MutableStateFlow<DataState<Boolean>>(DataState.Success(false))
//    val statsState: StateFlow<DataState<Boolean>> = _statsState

    fun getAllRecords(): Flow<List<Record>> {
        return recordDao.getAllRecords()
    }

    suspend fun addRecord(record: Record) {
        _operationState.value = DataState.Loading
        try {
            // 修正：使用正确的DAO方法名 insertRecord
            recordDao.insertRecord(record)
            _operationState.value = DataState.Success(Unit)
        } catch (e: Exception) {
            _operationState.value = DataState.Error("添加记录失败: ${e.message}")
        }
    }

    suspend fun updateRecord(record: Record) {
        _operationState.value = DataState.Loading
        try {
            // 修正：使用正确的DAO方法名 updateRecord
            recordDao.updateRecord(record)
            _operationState.value = DataState.Success(Unit)
        } catch (e: Exception) {
            _operationState.value = DataState.Error("更新记录失败: ${e.message}")
        }
    }

    suspend fun deleteRecord(record: Record) {
        _operationState.value = DataState.Loading
        try {
            // 修正：使用正确的DAO方法名 deleteRecord
            recordDao.deleteRecord(record)
            _operationState.value = DataState.Success(Unit)
        } catch (e: Exception) {
            _operationState.value = DataState.Error("删除记录失败: ${e.message}")
        }
    }
    // 新增：根据ID获取记录 V0.4
    // 修复：根据ID获取记录 - 直接调用DAO，不要循环调用
    suspend fun getRecordById(id: String): Record? {
        return try {
            recordDao.getRecordById(id)
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getTotalExpenses(): Double {
        return withContext(Dispatchers.IO) {
            recordDao.getTotalExpenses() ?: 0.0
        }
    }

    suspend fun getTotalIncome(): Double {
        return withContext(Dispatchers.IO) {
            recordDao.getTotalIncome() ?: 0.0
        }
    }
    suspend fun clearOperationState() {
        _operationState.value=DataState.Success(Unit)
    }
}