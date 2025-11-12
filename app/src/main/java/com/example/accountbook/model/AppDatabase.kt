package com.example.accountbook.model
//数据库
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(
    entities = [Record::class, User::class],
    version = 4,
    exportSchema = true
)
@TypeConverters(RecordTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            println("DEBUG: 初始化数据库") // 添加调试
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "accounting_database"
                )
                    .fallbackToDestructiveMigration()  // 添加这行，允许破坏性迁移
                    .build()
                INSTANCE = instance
                println("DEBUG: 数据库创建完成")
                instance
            }
        }
    }
}