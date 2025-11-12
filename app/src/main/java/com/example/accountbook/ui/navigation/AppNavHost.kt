package com.example.accountbook.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.accountbook.ui.components.AddRecordScreen
import com.example.accountbook.ui.components.RecordListScreen
import com.example.accountbook.ui.components.LoginScreen
import com.example.accountbook.ui.components.RegisterScreen
import com.example.accountbook.ui.screens.StatisticsScreen
import com.example.accountbook.viewmodel.RecordViewModel
import com.example.accountbook.viewmodel.AuthViewModel
import com.example.accountbook.ui.components.EditRecordScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: RecordViewModel,
    authViewModel: AuthViewModel
) {
    // 在每个屏幕中使用独立的ViewModel实例
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    // 设置当前用户ID到RecordViewModel
                    viewModel.setCurrentUserId(user.id)
                    navController.navigate(Screen.RecordList.route) {
                        // 清除返回栈，确保用户切换时重新创建所有屏幕
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Screen.Login.route) },
                onNavigateToLogin = { navController.popBackStack() },
                viewModel = authViewModel
            )
        }

        composable(Screen.RecordList.route) {
            RecordListScreen(
                onAddRecordClick = { navController.navigate(Screen.AddRecord.route) },
                onStatsClick = { navController.navigate(Screen.Statistics.route) },
                onLogout = { 
                    // 清除当前用户状态
                    viewModel.clearCurrentUserId()
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        // 清除返回栈，确保登出时重新创建登录屏幕
                        popUpTo(Screen.RecordList.route) { inclusive = true }
                    }
                },
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