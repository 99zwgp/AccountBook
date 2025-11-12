package com.example.accountbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.accountbook.viewmodel.AuthViewModel
import com.example.accountbook.viewmodel.AuthViewModelFactory
import com.example.accountbook.model.AppDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (com.example.accountbook.model.User) -> Unit,
    onRegisterClick: () -> Unit,
    authViewModel: AuthViewModel
) {
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val uiState by authViewModel.uiState.collectAsState()
    
    // 监听登录成功事件
    LaunchedEffect(Unit) {
        authViewModel.events.collect { event ->
            when (event) {
                is com.example.accountbook.viewmodel.AuthEvent.LoginSuccess -> {
                    onLoginSuccess(event.user)
                }
                else -> {}
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 标题
        Text(
            text = "欢迎回来",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "登录您的账户",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // 错误提示
        uiState.errorMessage?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
        
        // 用户名/邮箱输入框
        OutlinedTextField(
            value = identifier,
            onValueChange = { identifier = it },
            label = { Text("用户名或邮箱") },
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = "用户名")
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            isError = uiState.errorMessage != null
        )
        
        // 密码输入框
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "密码")
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility 
                                     else Icons.Default.VisibilityOff,
                        contentDescription = "显示/隐藏密码"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None 
                                 else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            isError = uiState.errorMessage != null
        )
        
        // 登录按钮
        Button(
            onClick = {
                authViewModel.login(identifier, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading && identifier.isNotBlank() && password.isNotBlank()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("登录", style = MaterialTheme.typography.titleMedium)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 注册链接
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "还没有账户？",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            TextButton(
                onClick = onRegisterClick
            ) {
                Text("立即注册")
            }
        }
        
        // 快速登录提示（可选功能）
        if (identifier.isEmpty() && password.isEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "💡 提示：可以使用用户名或邮箱登录",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
        }
    }
}