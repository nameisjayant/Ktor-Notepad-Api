package com.codingwithjks.data.table

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object NotesTable : Table() {
    val id:Column<Int> = integer("id").autoIncrement()
    val userId:Column<Int> = integer("userId").references(UserTable.userId)
    val title:Column<String> = text("title")
    val note:Column<String> = text("note")
    val date:Column<Long> = long("date")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}