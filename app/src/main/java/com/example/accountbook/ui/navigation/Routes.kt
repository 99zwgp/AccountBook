package com.example.accountbook.ui.navigation

sealed class Screen(val route: String) {
    object RecordList : Screen("record_list")
    object AddRecord : Screen("add_record")
    object Statistics : Screen("statistics")

    // 可选：带参数的路由（为未来扩展准备）
    companion object {
        fun withArgs(vararg args: String): String {
            return buildString {
                append(args.joinToString("/"))
            }
        }
    }
}