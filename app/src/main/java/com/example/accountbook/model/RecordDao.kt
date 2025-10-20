package com.example.accountbook.model
//数据访问对象

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
@TypeConverters(RecordTypeConverter::class)
interface RecordDao {

    @Query("SELECT * FROM records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: String): Record?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query("DELETE FROM records")
    suspend fun deleteAllRecords()

    // 统计查询
    @Query("SELECT SUM(amount) FROM records WHERE type = 'EXPENSE'")
    suspend fun getTotalExpenses(): Double?

    @Query("SELECT SUM(amount) FROM records WHERE type = 'INCOME'")
    suspend fun getTotalIncome(): Double?

    // 分类统计
    @Query("SELECT category, SUM(amount) as total FROM records WHERE type = 'EXPENSE' GROUP BY category")
    suspend fun getExpensesByCategory(): List<CategoryAmount>
}

// 分类统计数据类
data class CategoryAmount(
    val category: String,
    val total: Double
)