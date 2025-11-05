package com.example.accountbook.ui.navigation

sealed class Screen(val route: String) {
    object RecordList : Screen("record_list")
    object AddRecord : Screen("add_record")
    object Statistics : Screen("statistics")

    object EditRecord : Screen("edit_record") {
        const val argRecordId = "recordId"
        val routeWithArgs = "$route/{$argRecordId}"

        fun createRoute(recordId: String) = "$route/$recordId"
    }
}