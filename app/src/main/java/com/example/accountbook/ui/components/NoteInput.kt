package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NoteInput(
    note: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = note,
        onValueChange = onNoteChange,
        label = { Text("备注") },
        modifier = modifier.fillMaxWidth(),
        singleLine = false,
        maxLines = 3
    )
}