package com.example.accountbook.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val email: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

// 用户登录请求数据类
data class LoginRequest(
    val identifier: String, // 可以是用户名或邮箱
    val password: String
)

// 用户注册请求数据类
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

// 认证状态枚举
enum class AuthState {
    UNAUTHENTICATED, // 未认证
    AUTHENTICATED,   // 已认证
    LOADING          // 加载中
}