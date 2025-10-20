package com.example.accountbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.accountbook.ui.components.AddRecordScreen
import com.example.accountbook.ui.components.RecordListScreen
import com.example.accountbook.ui.screens.StatisticsScreen
import com.example.accountbook.viewmodel.RecordViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: RecordViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.RecordList.route
    ) {
        // 记录列表页面
        composable(route = Screen.RecordList.route) {
            RecordListScreen(
                onAddRecordClick = {
                    navController.navigate(Screen.AddRecord.route)
                },
                onStatsClick = {
                    navController.navigate(Screen.Statistics.route)
                },
                viewModel = viewModel
            )
        }

        // 添加记录页面
        composable(route = Screen.AddRecord.route) {
            AddRecordScreen(
                onRecordSaved = {
                    // 保存成功后返回列表页
                    navController.popBackStack()
                },
                onCancel = {
                    // 取消时返回列表页
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        // 统计页面
        composable(route = Screen.Statistics.route) {
            StatisticsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}