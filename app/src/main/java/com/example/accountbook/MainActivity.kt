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

        setContent {
            AccountingNotebookTheme {
                val navController = rememberNavController()

                val viewModel: RecordViewModel = viewModel(
                    factory = RecordViewModelFactory(
                        RecordRepository(
                            AppDatabase.getInstance(this@MainActivity)
                        )
                    )
                )

                AppNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}