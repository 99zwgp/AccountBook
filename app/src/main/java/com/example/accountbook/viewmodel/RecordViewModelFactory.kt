package com.example.accountbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.accountbook.repository.RecordRepository

class RecordViewModelFactory(
    private val repository: RecordRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        println("DEBUG: RecordViewModelFactory 创建 ViewModel") // 添加调试
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)) {
            return RecordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}