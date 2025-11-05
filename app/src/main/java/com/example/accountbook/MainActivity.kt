package com.example.accountbook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.accountbook.model.AppDatabase
import com.example.accountbook.repository.RecordRepository
import com.example.accountbook.ui.navigation.AppNavHost
import com.example.accountbook.ui.theme.AccountingNotebookTheme
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

                // 先创建 Repository
                val repository = RecordRepository(
                    AppDatabase.getInstance(this)
                )
                println("DEBUG: Repository 创建完成") // 添加调试

                val viewModel: RecordViewModel = viewModel(
//                    factory = RecordViewModelFactory(
//                        RecordRepository(
//                            AppDatabase.getInstance(this@MainActivity)
//                        )
//                    )
                    factory = RecordViewModelFactory(repository)
                )
                println("DEBUG: ViewModel 创建完成") // 添加调试

                AppNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
                println("DEBUG: AppNavHost 设置完成") // 添加调试
            }
        }
    }
}