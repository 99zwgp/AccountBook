package com.example.accountbook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountbook.model.*
import com.example.accountbook.repository.AuthRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // UI状态管理
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    // 认证状态
    val authState: StateFlow<AuthState> = authRepository.authState
    val currentUser: StateFlow<User?> = authRepository.currentUser
    
    // 事件处理
    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()
    
    init {
        viewModelScope.launch {
            // 监听认证状态变化
            authRepository.authState.collect { state ->
                _uiState.update { it.copy(isLoading = state == AuthState.LOADING) }
            }
        }
        
        viewModelScope.launch {
            // 监听用户状态变化
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }
    
    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val loginRequest = LoginRequest(identifier, password)
            val result = authRepository.login(loginRequest)
            
            result.onSuccess { user ->
                _events.emit(AuthEvent.LoginSuccess(user))
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _events.emit(AuthEvent.LoginError(error.message ?: "登录失败"))
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }
    
    fun register(username: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            val registerRequest = RegisterRequest(username, email, password, confirmPassword)
            val result = authRepository.register(registerRequest)
            
            result.onSuccess { user ->
                _events.emit(AuthEvent.RegisterSuccess(user))
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _events.emit(AuthEvent.RegisterError(error.message ?: "注册失败"))
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _events.emit(AuthEvent.LogoutSuccess)
            _uiState.update { 
                it.copy(
                    currentUser = null,
                    errorMessage = null
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    fun updateCurrentUser(user: User?) {
        _uiState.update { it.copy(currentUser = user) }
    }
    
    fun isAuthenticated(): Boolean {
        return authRepository.isAuthenticated()
    }
    
    suspend fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()
    }
}

// UI状态数据类
data class AuthUiState(
    val isLoading: Boolean = false,
    val currentUser: User? = null,
    val errorMessage: String? = null
)

// 认证事件密封类
sealed class AuthEvent {
    data class LoginSuccess(val user: User) : AuthEvent()
    data class RegisterSuccess(val user: User) : AuthEvent()
    data class LoginError(val message: String) : AuthEvent()
    data class RegisterError(val message: String) : AuthEvent()
    object LogoutSuccess : AuthEvent()
}