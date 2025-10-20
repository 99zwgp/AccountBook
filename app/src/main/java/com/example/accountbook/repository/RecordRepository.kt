package com.example.accountbook.repository  // 确保包名正确

import com.example.accountbook.model.AppDatabase
import com.example.accountbook.model.Record
import com.example.accountbook.model.RecordDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecordRepository(private val appDatabase: AppDatabase) {

    fun getAllRecords(): Flow<List<Record>> {
        return appDatabase.recordDao().getAllRecords()
    }

    suspend fun addRecord(record: Record) {
        withContext(Dispatchers.IO) {
            appDatabase.recordDao().insertRecord(record)
        }
    }

    suspend fun updateRecord(record: Record) {
        withContext(Dispatchers.IO) {
            appDatabase.recordDao().updateRecord(record)
        }
    }

    suspend fun deleteRecord(record: Record) {
        withContext(Dispatchers.IO) {
            appDatabase.recordDao().deleteRecord(record)
        }
    }

    suspend fun getTotalExpenses(): Double {
        return withContext(Dispatchers.IO) {
            appDatabase.recordDao().getTotalExpenses() ?: 0.0
        }
    }

    suspend fun getTotalIncome(): Double {
        return withContext(Dispatchers.IO) {
            appDatabase.recordDao().getTotalIncome() ?: 0.0
        }
    }
}