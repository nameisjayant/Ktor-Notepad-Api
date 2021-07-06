package com.codingwithjks.data.dao

import com.codingwithjks.data.model.User

interface UserDao  {

    suspend fun createUser(
        name:String,
        email:String,
        password:String
    ):User?

    suspend fun findUser(
        userId:Int
    ):User?

    suspend fun findUserByEmail(
        email: String
    ):User?

    suspend fun deleteUser(
        userId:Int
    ):Int

    suspend fun updateUser(
        userId:Int,
        name: String,
        email: String,
        password: String
    ):Int

    suspend fun getAllUser():List<User>

}