package com.example.accountbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.accountbook.ui.components.AddRecordScreen
import com.example.accountbook.ui.components.RecordListScreen
import com.example.accountbook.ui.screens.StatisticsScreen
import com.example.accountbook.viewmodel.RecordViewModel
import com.example.accountbook.ui.components.EditRecordScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: RecordViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.RecordList.route
    ) {
        composable(Screen.RecordList.route) {
            RecordListScreen(
                onAddRecordClick = { navController.navigate(Screen.AddRecord.route) },
                onStatsClick = { navController.navigate(Screen.Statistics.route) },
                viewModel = viewModel,
                onEditRecord = { recordId ->
                    navController.navigate(Screen.EditRecord.createRoute(recordId))
                }
            )
        }

        composable(Screen.AddRecord.route) {
            AddRecordScreen(
                onRecordSaved = { navController.popBackStack() },
                onCancel = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(
            route = Screen.EditRecord.routeWithArgs,
            arguments = listOf(navArgument(Screen.EditRecord.argRecordId) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString(Screen.EditRecord.argRecordId) ?: ""
            println("DEBUG: 进入编辑页面，记录ID: $recordId") // 添加调试
            EditRecordScreen(
                recordId = recordId,
                onRecordUpdated = {
                    println("DEBUG: 记录更新完成，返回列表")
                    navController.popBackStack()
                },
                onCancel = {
                    println("DEBUG: 取消编辑，返回列表")
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onBackClick = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}