package com.example.accountbook.model

import androidx.room.*

@Dao
interface UserDao {
    
    @Insert
    suspend fun insert(user: User)
    
    @Update
    suspend fun update(user: User)
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("SELECT * FROM users WHERE username = :identifier OR email = :identifier")
    suspend fun getUserByIdentifier(identifier: String): User?
    
    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun checkUsernameExists(username: String): Int
    
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun checkEmailExists(email: String): Int
    
    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, isActive: Boolean)
    
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)
}