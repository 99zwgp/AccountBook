package com.example.accountbook.repository

import com.example.accountbook.model.*
import kotlinx.coroutines.flow.*
import java.security.MessageDigest

class AuthRepository(
    private val database: AppDatabase
) {
    
    private val userDao = database.userDao()
    
    // 用户认证状态管理
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    // 认证状态
    private val _authState = MutableStateFlow(AuthState.UNAUTHENTICATED)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    suspend fun login(loginRequest: LoginRequest): Result<User> {
        return try {
            _authState.value = AuthState.LOADING
            
            // 验证输入
            if (loginRequest.identifier.isBlank() || loginRequest.password.isBlank()) {
                return Result.failure(IllegalArgumentException("用户名/邮箱和密码不能为空"))
            }
            
            // 查找用户
            val user = userDao.getUserByIdentifier(loginRequest.identifier)
            if (user == null) {
                return Result.failure(IllegalArgumentException("用户不存在"))
            }
            
            // 验证密码
            val inputPasswordHash = hashPassword(loginRequest.password)
            if (user.passwordHash != inputPasswordHash) {
                return Result.failure(IllegalArgumentException("密码错误"))
            }
            
            // 检查用户状态
            if (!user.isActive) {
                return Result.failure(IllegalArgumentException("用户已被禁用"))
            }
            
            // 登录成功
            _currentUser.value = user
            _authState.value = AuthState.AUTHENTICATED
            Result.success(user)
            
        } catch (e: Exception) {
            _authState.value = AuthState.UNAUTHENTICATED
            Result.failure(e)
        }
    }
    
    suspend fun register(registerRequest: RegisterRequest): Result<User> {
        return try {
            _authState.value = AuthState.LOADING
            
            // 验证输入
            if (registerRequest.username.isBlank() || registerRequest.email.isBlank() || 
                registerRequest.password.isBlank()) {
                return Result.failure(IllegalArgumentException("所有字段都不能为空"))
            }
            
            // 验证密码匹配
            if (registerRequest.password != registerRequest.confirmPassword) {
                return Result.failure(IllegalArgumentException("两次输入的密码不匹配"))
            }
            
            // 验证密码强度
            if (registerRequest.password.length < 6) {
                return Result.failure(IllegalArgumentException("密码长度至少6位"))
            }
            
            // 检查用户名是否已存在
            if (userDao.checkUsernameExists(registerRequest.username) > 0) {
                return Result.failure(IllegalArgumentException("用户名已存在"))
            }
            
            // 检查邮箱是否已存在
            if (userDao.checkEmailExists(registerRequest.email) > 0) {
                return Result.failure(IllegalArgumentException("邮箱已被注册"))
            }
            
            // 验证邮箱格式
            if (!isValidEmail(registerRequest.email)) {
                return Result.failure(IllegalArgumentException("邮箱格式不正确"))
            }
            
            // 创建新用户
            val newUser = User(
                username = registerRequest.username,
                email = registerRequest.email,
                passwordHash = hashPassword(registerRequest.password)
            )
            
            // 保存到数据库
            userDao.insert(newUser)
            
            _currentUser.value = newUser
            _authState.value = AuthState.AUTHENTICATED
            Result.success(newUser)
            
        } catch (e: Exception) {
            _authState.value = AuthState.UNAUTHENTICATED
            Result.failure(e)
        }
    }
    
    suspend fun logout() {
        _currentUser.value = null
        _authState.value = AuthState.UNAUTHENTICATED
    }
    
    suspend fun getCurrentUser(): User? {
        return _currentUser.value
    }
    
    fun isAuthenticated(): Boolean {
        return _authState.value == AuthState.AUTHENTICATED && _currentUser.value != null
    }
    
    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(bytes)
        return hash.joinToString("") { "%02x".format(it) }
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }
    
    // 初始化时检查是否已有登录用户（可选功能）
    suspend fun initialize() {
        // 这里可以实现自动登录或加载上次登录用户的功能
    }
}