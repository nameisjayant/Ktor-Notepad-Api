package com.codingwithjks.util

import com.codingwithjks.data.model.Notes

data class UserResponse(
    val status: Boolean?,
    val message: String?,
    val data: List<UserData>?
)

data class UserData(
    val userId: Int?,
    val name: String?,
    val email: String?,
    val token: String?
)

data class NoteResponse(
    val status: Boolean?,
    val message: String?,
    val data: List<Notes>?
)

data class NoteData(
    val note: String?,
    val title: String?,
    val date: Long?
)