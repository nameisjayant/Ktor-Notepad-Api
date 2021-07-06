package com.codingwithjks.data.dao

import com.codingwithjks.data.model.Notes

interface NotesDao {

    suspend fun createNote(
        userId: Int,
        title: String,
        note: String,
        date: Long
    ): Notes?

    suspend fun getAllNotes(
        userId: Int
    ): List<Notes>

    suspend fun deleteAllNotes(
        userId: Int
    ): Int

    suspend fun deleteNote(
        id: Int
    ): Int

    suspend fun updateNote(
        id: Int,
        title: String,
        note: String,
        date: Long
    ): Int
}