package com.example.accountbook.model
//类型转换器
import androidx.room.TypeConverter

class RecordTypeConverter {
    @TypeConverter
    fun fromRecordType(recordType: RecordType): String {
        return recordType.name
    }

    @TypeConverter
    fun toRecordType(recordTypeString: String): RecordType {
        return RecordType.valueOf(recordTypeString)
    }
}