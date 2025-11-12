package com.example.accountbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.accountbook.model.AppDatabase
import com.example.accountbook.repository.AuthRepository
import com.example.accountbook.repository.RecordRepository
import com.example.accountbook.ui.navigation.AppNavHost
import com.example.accountbook.ui.theme.AccountingNotebookTheme
import com.example.accountbook.viewmodel.AuthViewModel
import com.example.accountbook.viewmodel.AuthViewModelFactory
import com.example.accountbook.viewmodel.RecordViewModel
import com.example.accountbook.viewmodel.RecordViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("DEBUG: MainActivity onCreate") // 添加调试

        setContent {
            AccountingNotebookTheme {
                println("DEBUG: Compose 主题设置") // 添加调试

                val navController = rememberNavController()
                println("DEBUG: NavController 创建完成") // 添加调试

                // 创建数据库实例
                val database = AppDatabase.getInstance(this)
                println("DEBUG: 数据库实例创建完成") // 添加调试

                // 创建 Auth Repository 和 ViewModel
                val authRepository = AuthRepository(database)
                println("DEBUG: AuthRepository 创建完成") // 添加调试

                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(database)
                )
                println("DEBUG: AuthViewModel 创建完成") // 添加调试

                // 创建 Record Repository 和 ViewModel
                val recordRepository = RecordRepository(database)
                println("DEBUG: RecordRepository 创建完成") // 添加调试

                val recordViewModel: RecordViewModel = viewModel(
                    factory = RecordViewModelFactory(recordRepository)
                )
                println("DEBUG: RecordViewModel 创建完成") // 添加调试

                AppNavHost(
                    navController = navController,
                    viewModel = recordViewModel,
                    authViewModel = authViewModel
                )
                println("DEBUG: AppNavHost 设置完成") // 添加调试
            }
        }
    }
}