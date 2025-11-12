package com.example.accountbook.model
//数据访问对象

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
@TypeConverters(RecordTypeConverter::class)
interface RecordDao {


    @Query("SELECT * FROM records WHERE userId = :userId ORDER BY date DESC")
    fun getRecordsByUserId(userId: String): Flow<List<Record>>

    @Query("SELECT * FROM records WHERE id = :id AND userId = :userId")
    suspend fun getRecordById(id: String, userId: String): Record?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query("DELETE FROM records WHERE userId = :userId")
    suspend fun deleteAllRecordsByUserId(userId: String)

    // 统计查询
    @Query("SELECT SUM(amount) FROM records WHERE type = 'EXPENSE' AND userId = :userId")
    suspend fun getTotalExpenses(userId: String): Double?

    @Query("SELECT SUM(amount) FROM records WHERE type = 'INCOME' AND userId = :userId")
    suspend fun getTotalIncome(userId: String): Double?

    // 分类统计
    @Query("SELECT category, SUM(amount) as total FROM records WHERE type = 'EXPENSE' AND userId = :userId GROUP BY category")
    suspend fun getExpensesByCategory(userId: String): List<CategoryAmount>

    // 新增：批量更新方法（为未来功能准备）
    @Update
    suspend fun updateRecords(records: List<Record>)
}

// 分类统计数据类
data class CategoryAmount(
    val category: String,
    val total: Double
)

