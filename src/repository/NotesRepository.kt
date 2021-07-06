package com.codingwithjks.repository

import com.codingwithjks.data.dao.NotesDao
import com.codingwithjks.data.model.Notes
import com.codingwithjks.data.table.NotesTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class NotesRepository : NotesDao {
    override suspend fun createNote(userId: Int, title: String, note: String, date: Long): Notes? {
        var statement:InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = NotesTable.insert { notes->
                notes[NotesTable.userId] = userId
                notes[NotesTable.title] = title
                notes[NotesTable.note] = note
                notes[NotesTable.date] = date
            }
        }
        return rowToNote(statement?.resultedValues?.get(0))
    }

    override suspend fun getAllNotes(userId: Int): List<Notes> = DatabaseFactory.dbQuery {
        NotesTable.select { NotesTable.userId.eq(userId) }
            .mapNotNull {
                rowToNote(it)
            }
    }

    override suspend fun deleteAllNotes(userId: Int): Int = DatabaseFactory.dbQuery {
        NotesTable.deleteWhere { NotesTable.userId.eq(userId) }
    }

    override suspend fun deleteNote(id: Int): Int = DatabaseFactory.dbQuery {
        NotesTable.deleteWhere { NotesTable.id.eq(id) }
    }
    override suspend fun updateNote(id: Int, title: String, note: String, date: Long): Int = DatabaseFactory
        .dbQuery {
            NotesTable.update({NotesTable.id.eq(id)}){notes->
                notes[NotesTable.title] = title
                notes[NotesTable.note] = note
                notes[NotesTable.date] = date
            }
        }

    private fun rowToNote(row:ResultRow?) : Notes? {
        if(row == null)
            return null
        return Notes(
            id = row[NotesTable.id],
            userId = row[NotesTable.userId],
            title = row[NotesTable.title],
            note = row[NotesTable.note],
            date = row[NotesTable.date]
        )
    }
}